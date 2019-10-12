package ch.mailchimp.controller

import ch.database.{DataRead, DataReads, Entity}
import ch.mailchimp.database.ContactUpdate
import ch.mailchimp.model.{APIConfiguration, ContactList}
import ch.model.DataSet
import utopia.vault.database.Connection

import scala.concurrent.{ExecutionContext, Future}

/**
  * Used for updating mail chimp contact data based on latest contact data updates
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object UpdateContacts
{
	/**
	  * Updates all contact data
	  * @param exc Execution context
	  * @param connection DB connection
	  * @param configuration API connection configuration
	  * @return List of asynchronous operation completions
	  */
	def apply()(implicit exc: ExecutionContext, connection: Connection, configuration: APIConfiguration): Vector[Future[Unit]] =
	{
		// Finds data for all contact lists and updates each
		ch.mailchimp.database.model.ContactList.getAll().flatMap { apply(_) }
	}
	
	/**
	  * Updates contacts for the specified list
	  * @param list Targeted list
	  * @param exc Execution context
	  * @param connection DB connection
	  * @param configuration API connection configuration
	  * @return Futures for all contact data send operations that were started
	  */
	def apply(list: ContactList)(implicit exc: ExecutionContext, connection: Connection,
								 configuration: APIConfiguration) =
	{
		// println(s"Updating contacts for list $list")
		
		// Checks if contact data was updated after the last update event
		val lastUpdateEvent = ContactUpdate.lastContactUpdateEventFor(list.id)
		val reads = DataReads.forTypeWithId(list.contentTypeId)
		val updatedContactReads = lastUpdateEvent.map { update => reads.latestVersionsAfter(update.created) }
			.getOrElse { reads.latestVersions }
		
		// println(s"Found ${updatedContactReads.size} new contact reads")
		
		val updates = updatedContactReads.flatMap { reads =>
			
			val contactId = reads.head.targetId
			// Reads target entity data first
			val contactData = reads.map { read => DataRead(read.id).data }.reduce { _ ++ _ }
			
			if (contactData.nonEmpty)
			{
				// Then reads data of containing elements
				val containingEntities = Entity(contactId).parentEntities
				val otherData = containingEntities.reverse.map { e => Entity(e.id).latestData }
				
				// Combines all data together
				val allData: DataSet = if (otherData.isEmpty) contactData else otherData.reduce { _ ++ _ } ++ contactData
				
				// Pushes new data to MailChimp
				ContactsAPI.push(list, allData)
			}
			else
				None
		}
		
		// Saves a new update event to DB (only if there was new contact data)
		if (updates.nonEmpty)
		{
			// println("Recording new contact update event")
			ContactUpdate.insertEvent(list.id)
		}
		
		// println("List contact update completing")
		updates
	}
}
