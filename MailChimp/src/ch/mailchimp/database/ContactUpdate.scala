package ch.mailchimp.database

import ch.mailchimp.database.model.ContactUpdateEvent
import utopia.vault.database.Connection

/**
  * Used for interacting with contact (mail chimp) related DB data
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object ContactUpdate
{
	/**
	  * Inserts a new update event to database
	  * @param listId Targeted list id
	  * @param connection DB connection
	  * @return Generated event id
	  */
	def insertEvent(listId: Int)(implicit connection: Connection) = ContactUpdateEvent.forInsert(listId).insert().getInt
	
	/**
	  * @param listId Id of targetd list
	  * @param connection DB connection
	  * @return Last contact update event for that list
	  */
	def lastContactUpdateEventFor(listId: Int)(implicit connection: Connection) =
		ContactUpdateEvent.getMax("created", ContactUpdateEvent.withListId(listId).toCondition)
}
