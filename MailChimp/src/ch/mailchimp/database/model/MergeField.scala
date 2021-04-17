package ch.mailchimp.database.model

import ch.mailchimp.database.Tables
import utopia.flow.datastructure.immutable
import utopia.flow.datastructure.immutable.Constant
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.factory.FromValidatedRowModelFactory

object MergeField extends FromValidatedRowModelFactory[ch.mailchimp.model.MergeField]
{
	override def table = Tables.mergeField
	
	override protected def fromValidatedModel(valid: immutable.Model[Constant]) = ch.mailchimp.model.MergeField(
		valid("id").getInt, valid("list").getInt, valid("mergeId").getInt, valid("name").getString, valid("label").getInt)
}

/**
  * Used for searching & updating merge field data in DB
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
case class MergeField(id: Option[Int] = None, listId: Option[Int] = None, mergeId: Option[Int] = None,
					  name: Option[String] = None, labelId: Option[Int] = None)
	extends StorableWithFactory[ch.mailchimp.model.MergeField]
{
	override def factory = MergeField
	
	override def valueProperties = Vector("id" -> id, "list" -> listId, "mergeId" -> mergeId,
		"name" -> name, "label" -> labelId)
}
