package ch.database

import java.time.Instant

import utopia.flow.generic.ValueConversions._
import utopia.vault.sql.Extensions._
import ch.database.model.{ContactCompanyRole, ContactDataLabel, ContactDataRead, ContactRole}
import utopia.vault.database.Connection
import utopia.vault.sql.{ConditionElement, Delete, Select, Where}

/**
  * Used for interacting with contact DB data
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
// TODO: Refactor once there is only one target table left
object Contact extends DataInterface[model.ContactData, ContactDataRead, ContactDataLabel]
{
	// COMPUTED	------------------------
	
	private def contactTable = model.Contact.table
	private def roleTable = ContactRole.table
	private def roleLinkTable = ContactCompanyRole.table
	
	private def roleLink = roleLinkTable("role")
	private def contactLink = roleLinkTable("contact")
	private def companyLink = roleLinkTable("company")
	
	private def contactIndex = contactTable.primaryColumn.get
	private def linkTime = roleLinkTable("created")
	
	
	// IMPLEMENTED	-------------------
	
	override def dataFactory = model.ContactData
	override def readFactory = ContactDataRead
	override def labelFactory = ContactDataLabel
	
	
	// OTHER	-----------------------
	
	/**
	  * Inserts a new contact to DB
	  * @param sourceId Id of contact data origin
	  * @param connection DB connection
	  * @return New contact's id
	  */
	def insert(sourceId: Int)(implicit connection: Connection) = model.Contact.forInsert(sourceId).insert().getInt
	
	/**
	  * Reads contact data from DB
	  * @param contactId Contact identifier
	  * @param connection DB connection
	  * @return Contact data from DB. None if id didn't match any contact
	  */
	def withId(contactId: Int)(implicit connection: Connection) = model.Contact.getMany(contactIndex <=> contactId).headOption
	
	/**
	  * Adds new roles for a contact
	  * @param contactId Contact identifier
	  * @param companyId Company identifier
	  * @param newRoleIds Ids of new contact roles within company
	  * @param sourceId Data source id
	  * @param created Data creation time (default = current instant)
	  * @param connection DB connection
	  */
	def addRolesForContact(contactId: Int, companyId: Int, newRoleIds: Traversable[Int], sourceId: Int,
						   created: Instant = Instant.now())(implicit connection: Connection) =
	{
		newRoleIds.foreach { roleId =>
			ContactCompanyRole.forInsert(contactId, companyId, roleId, sourceId, created).insert() }
	}
	
	/**
	  * Removes certain roles from a contact
	  * @param contactId Contact identifier
	  * @param companyId Company identifier
	  * @param oldRoleIds The roles that should be removed from contact in company
	  * @param connection DB connection
	  */
	def removeRolesFromContact(contactId: Int, companyId: Int, oldRoleIds: Seq[Int])
							  (implicit connection: Connection): Unit =
	{
		if (oldRoleIds.nonEmpty)
		{
			val targetRoleIds: Seq[ConditionElement] = oldRoleIds.map
			{ id => id: ConditionElement }
			connection(Delete(roleLinkTable) + Where(
				ContactCompanyRole.withContactId(contactId).withCompanyId(companyId).toCondition &&
					roleLink.in(targetRoleIds)))
		}
	}
	
	/**
	  * Finds all contacts inside specified companies
	  * @param companyIds Ids of targeted companies
	  * @param addedAfter A time threshold that determines the oldest contact additions included
	  * @param connection DB Connection
	  * @return Set that contains ids of all found contacts
	  */
	def contactsWithin(companyIds: Set[Int], addedAfter: Option[Instant] = None)(implicit connection: Connection) =
	{
		if (companyIds.nonEmpty)
		{
			val baseCondition = (companyLink in companyIds.map { id => id: ConditionElement }) &&
				ContactRole.insideCompany.toCondition
			val finalCondition = addedAfter.map { time => baseCondition && (linkTime > time) }.getOrElse(baseCondition)
			
			connection(Select(roleLinkTable join roleTable, contactLink) + Where(finalCondition))
				.rows.flatMap { _.value.int }.toSet
		}
		else
			Set[Int]()
	}
}
