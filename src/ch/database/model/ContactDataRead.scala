package ch.database.model

import java.time.Instant

import ch.database.Tables

object ContactDataRead extends DataReadFactory[ContactDataRead]
{
	// IMPLEMENTED	----------------
	
	override def targetPropertyName = "contact"
	
	override def table = Tables.contactDataRead
	
	
	// OTHER	--------------------
	
	/**
	  * @param contactId Contact id
	  * @return Model with only contact id set
	  */
	def withContactId(contactId: Int) = withTargetId(contactId)
}

/**
  * Used for searching & updating contact data read DB data
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
case class ContactDataRead(id: Option[Int] = None, sourceId: Option[Int] = None, targetId: Option[Int] = None,
						   dataOriginTime: Option[Instant] = None, created: Option[Instant] = None) extends DataRead
{
	override def targetPropertyName = ContactDataRead.targetPropertyName
	
	override def factory = ContactDataRead
}
