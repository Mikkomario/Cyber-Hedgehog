package ch.model

import java.time.Instant

/**
  * Represents a contact's position in relation to a company
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  * @param id This assignment's unique identifier
  * @param contactId Id of associated contact
  * @param companyId Id of associated company
  * @param role Contact's role within the company
  * @param sourceId Id of the data source
  * @param since Time since contact relation was recorded
  */
case class ContactAssignment(id: Int, contactId: Int, companyId: Int, role: ContactRole, sourceId: Int, since: Instant)
{
	override def toString = s"$role company $companyId"
}
