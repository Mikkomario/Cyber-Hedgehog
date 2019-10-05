package ch.mailchimp.test

import java.time.Instant

import utopia.flow.util.TimeExtensions._
import ch.database.ConnectionPool
import ch.mailchimp.database.model.{ContactList, ContactSegment, MergeField}
import ch.util.ThreadPool
import utopia.flow.generic.DataType
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Throw

import scala.concurrent.ExecutionContext

/**
  * Simple tests list reading
  * @author Mikko Hilpinen
  * @since 21.7.2019, v0.1+
  */
object ReadListTest extends App
{
	DataType.setup()
	ErrorHandling.defaultPrinciple = Throw
	// Connection.modifySettings(_.copy(debugPrintsEnabled = true))
	
	assert(MergeField.tables.size == 1)
	assert(ContactSegment.tables.size == 1)
	assert(ContactList.tables.size == 3)
	assert(ContactList.joinedTables.size == 2)
	
	println(ContactList.target)
	
	val startTime = Instant.now()
	
	// Runs the operation
	implicit val exc: ExecutionContext = ThreadPool.executionContext
	ConnectionPool { implicit connection =>
	
		println("Reading list data")
		val lists = ContactList.getAll()
		
		assert(lists.nonEmpty)
		assert(lists.head.mergeFields.nonEmpty)
		assert(lists.head.segments.nonEmpty)
		
		lists.foreach(println)
	}
	println(s"Success!. Took ${(Instant.now() - startTime).description}")
}
