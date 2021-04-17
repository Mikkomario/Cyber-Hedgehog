package ch.mailchimp.database.model

import java.time.Instant

import utopia.flow.generic.ValueConversions._
import ch.mailchimp.database.Tables
import utopia.flow.datastructure.immutable
import utopia.flow.datastructure.immutable.Constant
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.factory.FromValidatedRowModelFactory

object ContactUpdateEvent extends FromValidatedRowModelFactory[ch.mailchimp.model.ContactUpdateEvent]
{
	// IMPLEMENTED	------------------
	
	override def table = Tables.contactUpdateEvent
	
	override protected def fromValidatedModel(valid: immutable.Model[Constant]) = ch.mailchimp.model.ContactUpdateEvent(
		valid("id").getInt, valid("list").getInt, valid("created").getInstant)
	
	
	// OTHER	----------------------
	
	/**
	  * Creates a model ready to be inserted to DB
	  * @param listId Target list id
	  */
	def forInsert(listId: Int) = ContactUpdateEvent(listId = Some(listId))
	
	/**
	  * @param listId Id of updated list
	  * @return Model with only list id set
	  */
	def withListId(listId: Int) = ContactUpdateEvent(listId = Some(listId))
}

/**
  * Used for searching & updating contact update event DB data
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
case class ContactUpdateEvent(id: Option[Int] = None, listId: Option[Int] = None, created: Option[Instant] = None)
	extends StorableWithFactory[ch.mailchimp.model.ContactUpdateEvent]
{
	override def factory = ContactUpdateEvent
	
	override def valueProperties = Vector("id" -> id, "list" -> listId, "created" -> created)
}
