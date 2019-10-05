package ch.test

import ch.controller.Profiler
import ch.database.ConnectionPool
import ch.util.ThreadPool
import utopia.flow.generic.DataType
import utopia.vault.util.ErrorHandling
import utopia.vault.util.ErrorHandlingPrinciple.Throw

import scala.concurrent.ExecutionContext

/**
  * Performs a company profiling once
  * @author Mikko Hilpinen
  * @since 19.7.2019, v0.1+
  */
object ProfilingTest extends App
{
	DataType.setup()
	ErrorHandling.defaultPrinciple = Throw
	
	println("Starting profiling. This may take a while...")
	implicit val exc: ExecutionContext = ThreadPool.executionContext
	ConnectionPool { implicit connection => Profiler.run() }
	println("Profiling complete!")
}
