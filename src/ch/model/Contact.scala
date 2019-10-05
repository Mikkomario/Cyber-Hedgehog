package ch.model

/**
  * Represents a company contact
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  * @param id This contact's unique identifier
  * @param assignments This contact's assignments across various companies
  * @param sourceId Id of this contact's origin source
  */
case class Contact(id: Int, assignments: Vector[ContactAssignment], sourceId: Int)
{
	// COMPUTED	----------------
	
	/**
	  * @return The id's of companies associated with this contact
	  */
	def companyIds = assignments.map { _.companyId }.toSet
	
	
	// IMPLEMENTED	------------
	
	override def toString = s"Contact $id (assignments: [${assignments.mkString(", ")}])"
	
	
	// OTHER	----------------
	
	/**
	  * @param companyId Id of targeted company
	  * @return This contact's role(s) in relation to the specified company
	  */
	def rolesWithinCompany(companyId: Int) = assignments.filter { _.companyId == companyId }.map { _.role }.toSet
}
