package ch.database.model

import ch.database.Tables
import ch.model.DataType

@deprecated("Replaced with EntityLabel", "v2")
object ContactDataLabel extends DataLabelFactory[ContactDataLabel]
{
	override def table = Tables.contactDataLabel
}

/**
  * Used for searching & updating contact label DB data
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
@deprecated("Replaced with EntityLabel", "v2")
case class ContactDataLabel(id: Option[Int] = None, dataType: Option[DataType] = None,
							isIdentifier: Option[Boolean] = None, isEmail: Option[Boolean] = None) extends DataLabel
{
	override def factory = ContactDataLabel
}
