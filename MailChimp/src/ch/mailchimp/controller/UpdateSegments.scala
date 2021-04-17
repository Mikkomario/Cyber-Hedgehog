package ch.mailchimp.controller

import utopia.flow.time.TimeExtensions._
import ch.database.{Entities, Entity, Profiling}
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
		
			val includedEntityIds = Profiling(latestProfiling.id).contentIds.toSet
			val previousEntityIds = lastUpdate.map { last => Profiling(last.profilingId).contentIds.toSet }.getOrElse(Set())
			
			// Finds the contact changes caused by profiling updates
			val (newContacts, contactsToRemove) =
			{
				// If the two profiling results were different, updates segments
				if (includedEntityIds != previousEntityIds)
				{
					val newEntityIds = includedEntityIds -- previousEntityIds
					val entityIdsToRemove = previousEntityIds -- includedEntityIds
					
					// Finds segment targets within each linked entity
					val newSegmentContacts = Entities.ofTypeWithId(list.contentTypeId).withinOther(newEntityIds)
					val contactsToRemove = Entities.ofTypeWithId(list.contentTypeId).withinOther(entityIdsToRemove)
					
					newSegmentContacts -> contactsToRemove
				}
				else
					Set[ch.model.Entity]() -> Set[ch.model.Entity]()
			}
			
			// Also checks for contacts that were added to accepted companies after the last profiling
			val contactsAddedAfterLastProfile = Entities.ofTypeWithId(list.contentTypeId).withinOther(
				includedEntityIds, Some(latestProfiling.time))
			
			// Finds contact email addresses
			val newSegmentEmails = (newContacts ++ contactsAddedAfterLastProfile).flatMap { c => Entity(c.id).email }
			val emailsToRemove = contactsToRemove.flatMap { c => Entity(c.id).email }
			
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
