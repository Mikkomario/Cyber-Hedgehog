package ch.mailchimp.controller

import utopia.flow.util.TimeExtensions._
import ch.database.{Contact, Profiling}
import ch.mailchimp.database.SegmentUpdate
import ch.mailchimp.model.{APIConfiguration, ContactList, ContactSegment}
import utopia.vault.database.Connection

import scala.concurrent.{ExecutionContext, Future}

/**
  * Used for updating mail chimp segment data with latest profiling results
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object UpdateSegments
{
	def apply()(implicit exc: ExecutionContext, connection: Connection,
				configuration: APIConfiguration): Vector[Future[Unit]] =
	{
		ch.mailchimp.database.model.ContactList.getAll().flatMap { apply(_) }
	}
	
	/**
	  * Updates all segments in the specified list
	  * @param list Targeted list
	  * @param exc Execution context
	  * @param connection DB connection
	  * @param configuration API interface configuration
	  * @return All data send processes that were started
	  */
	def apply(list: ContactList)(implicit exc: ExecutionContext, connection: Connection,
								 configuration: APIConfiguration): Vector[Future[Unit]] =
	{
		// Updates each segment in the list
		list.segments.flatMap { segment => apply(list, segment) }
	}
	
	private def apply(list: ContactList, segment: ContactSegment)
					 (implicit exc: ExecutionContext, connection: Connection, configuration: APIConfiguration) =
	{
		// Checks last segment update time
		val lastUpdate = SegmentUpdate.lastSegmentUpdateEventFor(segment.id)
		
		// Finds the latest profiling performed after last update
		val latestProfiling = Profiling.segment(segment.profilingSegmentId).latestProfiling.filter {
			profiling => lastUpdate.forall { _.created < profiling.time } }
		
		// If there was a new profiling made, compares it to the profiling used in the last update
		latestProfiling.flatMap { latestProfiling =>
		
			val includedCompanyIds = Profiling(latestProfiling.id).companyIds.toSet
			val previousCompanyIds = lastUpdate.map { last => Profiling(last.profilingId).companyIds.toSet }.getOrElse(Set())
			
			// Finds the contact changes caused by profiling updates
			val (newContactIds, contactIdsToRemove) =
			{
				// If the two profiling results were different, updates segments
				if (includedCompanyIds != previousCompanyIds)
				{
					val newCompanyIds = includedCompanyIds -- previousCompanyIds
					val companyIdsToRemove = previousCompanyIds -- includedCompanyIds
					
					// Finds contacts within each linked company
					val newSegmentContacts = Contact.contactsWithin(newCompanyIds)
					val contactsToRemove = Contact.contactsWithin(companyIdsToRemove)
					
					newSegmentContacts -> contactsToRemove
				}
				else
					Set[Int]() -> Set[Int]()
			}
			
			// Also checks for contacts that were added to accepted companies after the last profiling
			val contactsAddedAfterLastProfile = Contact.contactsWithin(includedCompanyIds, Some(latestProfiling.time))
			
			// Finds contact email addresses
			val newSegmentEmails = (newContactIds ++ contactsAddedAfterLastProfile).flatMap { Contact.emailForId(_) }
			val emailsToRemove = contactIdsToRemove.flatMap { Contact.emailForId(_) }
			
			// Sends data to MailChimp API
			val updateProcess =
			{
				if (newSegmentEmails.nonEmpty || emailsToRemove.nonEmpty)
					Some(SegmentsAPI.updateSegmentMembers(list, segment, newSegmentEmails.toVector, emailsToRemove.toVector))
				else
					None
			}
			
			// Saves a new update event
			SegmentUpdate.insertEvent(segment.id, latestProfiling.id)
			
			updateProcess
		}
	}
}
