package ch.mailchimp.controller

import ch.database.{Company, Contact}
import ch.mailchimp.database.ContactUpdate
import ch.mailchimp.model.{APIConfiguration, ContactList}
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
		val updatedContactReads = lastUpdateEvent.map { update => Contact.lastReadsAfter(update.created) }
			.getOrElse { Contact.lastReads }
		
		// println(s"Found ${updatedContactReads.size} new contact reads")
		
		val updates = updatedContactReads.flatMap { contactRead =>
			
			// Reads latest data for each contact and also finds data for the last company the contact worked within
			Contact.withId(contactRead.targetId).flatMap { contact =>
				
				val contactData = Contact.readData(contactRead.id)
				
				val companyId = contact.assignments.filter { _.role.isInsideCompany }.sortBy {
					_.since }.headOption.map { _.companyId }
				val companyData = companyId.flatMap { Company.lastReadForTargetId(_) }.map {
					read => Company.readData(read.id) }
				
				// println(s"Sending data for contact ${contact.id} (of company $companyId)")
				ContactsAPI.push(list, contactData, companyData)
			}
		}
		
		// Saves a new update event to DB (only if there was new contact data)
		if (updates.nonEmpty) {
			// println("Recording new contact update event")
			ContactUpdate.insertEvent(list.id)
		}
		
		// println("List contact update completing")
		updates
	}
}
