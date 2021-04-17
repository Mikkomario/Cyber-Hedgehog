package ch.mailchimp.test

import java.time.Instant

import utopia.flow.util.CollectionExtensions._
import utopia.flow.async.AsyncExtensions._
import utopia.flow.time.TimeExtensions._
import ch.database.ConnectionPool
import ch.mailchimp.controller.UpdateContacts
import ch.mailchimp.model.APIConfiguration
import ch.util.{Log, ThreadPool}
import utopia.disciple.apache.Gateway
import utopia.flow.generic.DataType
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Throw

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  * This test updates contact information to MailChimp
  * @author Mikko Hilpinen
  * @since 21.7.2019, v0.1+
  */
object ContactUpdateTest extends App
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
		
		println("Reading API configuration")
		APIConfiguration.fromSettings.map { implicit configuration =>
			
			println("Starting the update process. This may take a while...")
			val updateProcesses = UpdateContacts()
			println(s"Waiting for update(s) to finish. Updates started: ${updateProcesses.size}")
			updateProcesses.future.waitFor(180.seconds) match
			{
				case Success(results) =>
					val failures = results.flatMap { _.failure }
					println(s"Update(s) completed. ${results.size - failures.size}/${results.size} were successes")
					failures.foreach { Log(_) }
				case Failure(exception) => Log(exception, "Updates failed")
			}
			
		}.failure.foreach { Log(_, "Api configuration failed") }
	}
	println(s"Update process completed. Took ${(Instant.now() - startTime).description}")
}
