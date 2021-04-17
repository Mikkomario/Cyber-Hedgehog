package ch.backend.test

import java.time.Instant

import utopia.flow.util.CollectionExtensions._
import utopia.flow.async.AsyncExtensions._
import utopia.flow.time.TimeExtensions._
import ch.backend.controller.UpdateOnce
import ch.database.ConnectionPool
import ch.util.{Log, ThreadPool}
import utopia.disciple.apache.Gateway
import utopia.flow.generic.DataType
import utopia.vault.util.ErrorHandlingPrinciple.Throw
import utopia.vault.util.ErrorHandling

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  * This test performs the update process once and then quits
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object RunOnceTest extends App
{
	// Sets up basic settings
	DataType.setup()
	ErrorHandling.defaultPrinciple = Throw
	Gateway.maxConnectionsPerRoute = 5
	Gateway.maxConnectionsTotal = 15
	
	val startTime = Instant.now()
	
	// Runs the operation
	implicit val exc: ExecutionContext = ThreadPool.executionContext
	ConnectionPool { implicit connection =>
		
		println("Starting the update process. This may take a while...")
		UpdateOnce() match
		{
			case Success(posts) =>
				println("Waiting for final posts to complete...")
				posts.future.waitFor(180.seconds) match
				{
					case Success(results) =>
						val failures = results.flatMap { _.failure }
						println(s"Posts completed ${results.size - failures.size}/${results.size} were successes")
						failures.foreach { Log(_) }
					case Failure(error) => Log(error, "Posts failed to complete")
				}
				
			case Failure(error) => Log(error, "Update process failed")
		}
	}
	println(s"Update process completed. Took ${(Instant.now() - startTime).description}")
}
