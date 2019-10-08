package ch.database.model

import ch.database.Tables
import utopia.flow.datastructure.immutable.Value

@deprecated("Replaced with EntityData", "v2")
object ContactData extends DataFactory[ContactData]
{
	// IMPLEMENTED	----------------------
	
	override def table = Tables.contactData
	
	override def readFactory = ContactDataRead
	
	override def labelFactory = ContactDataLabel
}

/**
  * Used for searching & updating contact DB data
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
@deprecated("Replaced with EntityData", "v2")
case class ContactData(id: Option[Int] = None, readId: Option[Int], labelId: Option[Int] = None,
					   value: Option[Value] = None) extends Data[ContactData]
{
	// IMPLEMENTED	---------------------
	
	override def factory = ContactData
	
	override def makeCopy(id: Option[Int], readId: Option[Int], labelId: Option[Int], value: Option[Value]) =
		copy(id, readId, labelId, value)
}
