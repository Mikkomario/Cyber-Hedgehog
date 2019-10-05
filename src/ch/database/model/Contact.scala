package ch.database.model

import utopia.flow.generic.ValueConversions._
import ch.database.Tables
import utopia.vault.model.immutable.factory.FromResultFactory
import utopia.vault.model.immutable.{Result, Storable}

object Contact extends FromResultFactory[ch.model.Contact]
{
	// IMPLEMENTED	-----------------
	
	override def table = Tables.contact
	
	override def joinedTables = ContactCompanyRole.tables
	
	override def apply(result: Result) =
	{
		// Groups rows based on contact id
		val rowsPerContact = result.rows.groupBy { _.indexForTable(table).getInt }
		
		// There may be multiple company role links for each contact, parses all
		rowsPerContact.toVector.flatMap
		{
			case (id, rows) =>
				
				val sourceId = rows.head(table)("source").int
				
				if (sourceId.isDefined)
				{
					val assignments = rows.flatMap { r => ContactCompanyRole(r) }
					Some(ch.model.Contact(id, assignments, sourceId.get))
				}
				else
					None
		}
	}
	
	
	// OTHER	---------------------
	
	/**
	  * Creates a contact ready to be inserted to DB
	  * @param sourceId Identifier of contact data source
	  * @return Model ready to be inserted
	  */
	def forInsert(sourceId: Int) = Contact(sourceId = Some(sourceId))
	
	/**
	  * @param id Contact id
	  * @return A model with id set
	  */
	def withId(id: Int) = Contact(id = Some(id))
}

/**
  * Used for searching & updating contact data
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
case class Contact(id: Option[Int] = None, sourceId: Option[Int] = None) extends Storable
{
	override def table = Contact.table
	
	override def valueProperties = Vector("id" -> id, "source" -> sourceId)
}
