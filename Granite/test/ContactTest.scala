
import ch.database
import ch.database.{Company, ConnectionPool, Contact}
import ch.granite.model.Granite
import ch.util.ThreadPool
import utopia.flow.generic.DataType

import scala.concurrent.ExecutionContext

/**
  * Tests contact DB functions
  * @author Mikko Hilpinen
  * @since 14.7.2019, v0.1+
  */
object ContactTest extends App
{
	DataType.setup()
	
	val sourceId = Granite.id
	
	implicit val exc: ExecutionContext = ThreadPool.executionContext
	ConnectionPool { implicit connection =>
	
		// Adds a new company
		val companyId = Company.insert(sourceId)
		
		// Adds a new contact
		val contactId = Contact.insert(sourceId)
		
		// Reads contact data (must be found)
		val contact = Contact.withId(contactId).get
		println(s"Original status: $contact")
		
		assert(contact.id == contactId)
		assert(contact.rolesWithinCompany(companyId).isEmpty)
		
		// Adds contact roles
		Contact.addRolesForContact(contactId, companyId, Vector(2, 3), sourceId)
		
		// Reads current data
		val contactV2 = Contact.withId(contactId).get
		val rolesV2 = contactV2.rolesWithinCompany(companyId).toVector.map { _.id }.sorted
		println(s"Status after role addition: $contactV2")
		
		assert(rolesV2.nonEmpty)
		assert(rolesV2 == Vector(2, 3))
		
		// Removes a role
		Contact.removeRolesFromContact(contactId, companyId, Vector(3))
		
		// Reads current data
		val contactV3 = Contact.withId(contactId).get
		val rolesV3 = contactV3.rolesWithinCompany(companyId).toVector.map { _.id }.sorted
		println(s"Status after role removal: $contactV3")
		
		assert(rolesV3.nonEmpty)
		assert(rolesV3.size == 1)
		assert(rolesV3.head == 2)
		
		// Deletes test data
		database.model.Company.withId(companyId).delete()
		database.model.Contact.withId(contactId).delete()
	}
	
	println("Success!")
}
