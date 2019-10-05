package ch.test

import ch.controller.RiskScorer
import ch.database.ConnectionPool
import ch.util.ThreadPool
import utopia.flow.generic.DataType

import scala.concurrent.ExecutionContext

/**
 * Performs company cyber risk scoring once
 * @author Mikko Hilpinen
 * @since 28.8.2019, v1.1
 */
object RunScoring extends App
{
	DataType.setup()
	println("Starting scoring")
	
	// Runs the operation
	implicit val exc: ExecutionContext = ThreadPool.executionContext
	ConnectionPool { implicit connection =>
		RiskScorer.run()
	}
	
	println("Scoring complete")
}
