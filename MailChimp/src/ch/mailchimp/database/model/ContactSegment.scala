package ch.mailchimp.database.model

import utopia.flow.generic.ValueConversions._
import ch.mailchimp.database.Tables
import utopia.flow.datastructure.immutable
import utopia.flow.datastructure.immutable.Constant
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.factory.FromValidatedRowModelFactory

object ContactSegment extends FromValidatedRowModelFactory[ch.mailchimp.model.ContactSegment]
{
	// IMPLEMENTED	------------------
	
	override def table = Tables.contactSegment
	
	override protected def fromValidatedModel(valid: immutable.Model[Constant]) = ch.mailchimp.model.ContactSegment(
		valid("id").getInt, valid("list").getInt, valid("profilingSegment").getInt, valid("mailChimpSegmentId").getString)
}

/**
  * Used for searching & updating contact segment data in DB
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
case class ContactSegment(id: Option[Int] = None, listId: Option[Int] = None, profilingSegmentId: Option[Int] = None,
						  mailChimpSegmentId: Option[String] = None)
	extends StorableWithFactory[ch.mailchimp.model.ContactSegment]
{
	override def factory = ContactSegment
	
	override def valueProperties = Vector("id" -> id, "list" -> listId, "profilingSegment" -> profilingSegmentId,
		"mailChimpSegmentId" -> mailChimpSegmentId)
}
