package ch.backend.main

import utopia.flow.time.TimeExtensions._
import utopia.flow.async.AsyncExtensions._
import utopia.flow.util.CollectionExtensions._
import ch.backend.controller.UpdateOnce
import ch.backend.util.BackEndSettings
import ch.controller.RiskScorer
import ch.database.ConnectionPool
import ch.util.{Log, Settings, ThreadPool}
import utopia.disciple.apache.Gateway
import utopia.flow.async.Loop
import utopia.flow.generic.DataType
import utopia.vault.database.Connection
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Custom

import scala.concurrent.ExecutionContext
import scala.io.StdIn
import scala.util.{Failure, Success}

/**
  * The main app for the backend portion of this project
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object CHBackEnd extends App
{
	// Sets up basic settings
	DataType.setup()
	
	Connection.modifySettings { _.copy(password = Settings.Database.password, driver = Settings.Database.driver) }
	ErrorHandling.defaultPrinciple = Custom { Log(_) }
	
	Gateway.maxConnectionsPerRoute = 5
	Gateway.maxConnectionsTotal = 15
	
	private def completeOnce() =
	{
		println("Update started")
		// Runs the operation
		implicit val exc: ExecutionContext = ThreadPool.executionContext
		ConnectionPool.tryWith { implicit connection =>
			
			UpdateOnce() match
			{
				case Success(posts) =>
					println("Waiting for data posts to complete. Performing cyber risk scoring")
					// Performs cyber risk scoring while waiting for posts to finish
					RiskScorer.run()
					println("Cyber risk scoring complete")
					
					// Handles asynchronous post results
					posts.future.waitFor(180.seconds) match
					{
						case Success(results) =>
							println("Data posts completed")
							results.flatMap { _.failure }.foreach { Log(_) }
						case Failure(error) => Log(error, "Posts failed to complete")
					}
				
				case Failure(error) => Log(error, "Update process failed")
			}
			
		}.failure.foreach { Log(_, "Update process failed") }
	}
	
	private val interval = BackEndSettings.updateIntervalMinutes.minutes
	private val loop = Loop(interval) { completeOnce() }
	
	// Continues the loop in the background. Reads user input in the meanwhile and exits once user inputs 'exit' or an empty line
	private implicit val exc: ExecutionContext = ThreadPool.executionContext
	println("Starting Cyber Hedgehog updates in the background.")
	println(s"Updates will be performed every ${interval.description}")
	loop.startAsync()
	
	println("\nType 'exit' or an empty line to stop updates and quit.")
	private var readLine = "nonEmpty"
	while (readLine.nonEmpty && readLine != "exit")
	{
		readLine = StdIn.readLine()
	}
	
	println("Stopping the update process...")
	loop.stop().waitFor(180.seconds).failure.foreach { e =>
		println("Failed to stop the update. Forcing shutdown.")
		e.printStackTrace()
	}
	
	println("Update process stopped, shutting down")
	System.exit(0)
}
