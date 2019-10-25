package ch.test

import ch.database.{ConnectionPool, Entities, Entity}
import ch.util.{Log, Settings, ThreadPool}
import utopia.flow.generic.DataType
import utopia.vault.database.Connection
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Custom

import scala.concurrent.ExecutionContext

/**
 * Retrieves all data for all entities and prints it
 * @author Mikko Hilpinen
 * @since 25.10.2019, v2+
 */
object ReadEntityDataTest extends App
{
	// Sets up basic settings
	DataType.setup()
	Connection.modifySettings { _.copy(password = Settings.Database.password, driver = Settings.Database.driver) }
	ErrorHandling.defaultPrinciple = Custom { Log(_) }
	
	implicit val exc: ExecutionContext = ThreadPool.executionContext
	ConnectionPool { implicit connection =>
		
		// Reads data for all entities
		Entities.all.foreach { e =>
			val data = Entity(e.id).latestData
			println(s"Entity ${e.id} of type ${e.typeId}: $data")
		}
		println()
	}
	
	println("Completed")
}
