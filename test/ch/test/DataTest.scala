package ch.test

import ch.database.{ConnectionPool, Contact, Tables}
import ch.database.model.ContactData
import ch.util.ThreadPool
import utopia.flow.generic.DataType
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Throw

import scala.concurrent.ExecutionContext

/**
  * Tests DB data
  * @author Mikko Hilpinen
  * @since 21.7.2019, v0.1+
  */
object DataTest extends App
{
	DataType.setup()
	ErrorHandling.defaultPrinciple = Throw
	
	println("Starting test")
	
	implicit val exc: ExecutionContext = ThreadPool.executionContext
	ConnectionPool { implicit connection =>
	
		val data = ContactData.getAll()
		println(data.mkString("\n"))
		data.foreach { d => assert(!d.value.getString.contains("\"")) }
		
		println()
		
		val contactIds = Tables.contact.allIndices.map { _.getInt }
		val emails = contactIds.flatMap { Contact.emailForId(_) }
		println(emails.mkString("\n"))
		emails.foreach { e => assert(!e.contains("\"")) }
	}
	
	println("Success!")
}
