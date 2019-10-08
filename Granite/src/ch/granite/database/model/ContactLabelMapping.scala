package ch.granite.database.model

import ch.database.model.ContactDataLabel
import ch.granite.database.Tables

@deprecated("Replaced with FieldLabelMapping and OptionLabelMapping", "v2")
object ContactLabelMapping extends LabelMappingFactory[ContactLabelMapping]
{
	override def table = Tables.contactLabelMapping
	
	override protected def labelFactory = ContactDataLabel
	
	override def withFieldId(fieldId: Int) = ContactLabelMapping(fieldId = Some(fieldId))
	
	override def withOptionId(optionId: Int) = ContactLabelMapping(optionId = Some(optionId))
}

/**
  * Used for searching & updating contact label mappings in DB
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
@deprecated("Replaced with FieldLabelMapping and OptionLabelMapping", "v2")
case class ContactLabelMapping(id: Option[Int] = None, fieldId: Option[Int] = None, optionId: Option[Int] = None,
						  labelId: Option[Int] = None) extends LabelMapping
{
	override def factory = ContactLabelMapping
}
