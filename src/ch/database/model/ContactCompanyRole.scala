package ch.database.model

import java.time.Instant

import ch.database.Tables
import utopia.flow.generic.ValueConversions._
import ch.model.ContactAssignment
import utopia.vault.model.immutable.factory.FromRowFactory
import utopia.vault.model.immutable.{Row, StorableWithFactory}

object ContactCompanyRole extends FromRowFactory[ContactAssignment]
{
	// IMPLEMENTED	-------------------
	
	override def apply(row: Row) =
	{
		// Role must be parseable
		ContactRole(row).flatMap
		{
			role =>
				
				val model = row(table)
				val id = model("id").int
				val contactId = model("contact").int
				val companyId = model("company").int
				val sourceId = model("source").int
				val created = model("created").instant
				
				if (Vector(id, contactId, companyId, sourceId, created).forall { _.isDefined })
					Some(ContactAssignment(id.get, contactId.get, companyId.get, role, sourceId.get, created.get))
				else
					None
		}
	}
	
	override def table = Tables.contactCompanyRole
	
	override def joinedTables = Vector(ContactRole.table)
	
	
	// OTHER	---------------------
	
	/**
	  * Creates a role connection ready to be inserted
	  * @param contactId Id of associated contact
	  * @param companyId Id of associated company
	  * @param roleId Id of contact's role
	  * @param sourceId Id of data source
	  * @param created Connection creation time (defaults to current time)
	  * @return A model ready to be inserted
	  */
	def forInsert(contactId: Int, companyId: Int, roleId: Int, sourceId: Int, created: Instant = Instant.now()) =
		ContactCompanyRole(None, Some(contactId), Some(companyId), Some(roleId), Some(sourceId), Some(created))
	
	/**
	  * @param contactId Contact identifier
	  * @return A model with contact id set
	  */
	def withContactId(contactId: Int) = ContactCompanyRole(contactId = Some(contactId))
}

/**
  * Used for searching & updating contact company relations
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
case class ContactCompanyRole(id: Option[Int] = None, contactId: Option[Int] = None, companyId: Option[Int] = None,
							  roleId: Option[Int] = None, sourceId: Option[Int] = None, created: Option[Instant] = None)
	extends StorableWithFactory[ContactAssignment]
{
	// IMPLEMENTED	---------------
	
	override def factory = ContactCompanyRole
	
	override def valueProperties = Vector("id" -> id, "contact" -> contactId, "company" -> companyId, "role" -> roleId,
		"source" -> sourceId, "created" -> created)
	
	
	// OTHER	-------------------
	
	/**
	  * @param companyId Company identifier
	  * @return A copy of this model with specified company id
	  */
	def withCompanyId(companyId: Int) = copy(companyId = Some(companyId))
}
