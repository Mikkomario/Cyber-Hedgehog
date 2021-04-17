import java.time.Instant

import utopia.flow.async.AsyncExtensions._
import utopia.flow.time.TimeExtensions._
import ch.granite.controller.ReadGraniteData
import ch.util.ThreadPool
import utopia.flow.generic.DataType

import scala.concurrent.ExecutionContext

/**
  * Reads data from granite API
  * @author Mikko Hilpinen
  * @since 14.7.2019, v0.1+
  */
object GraniteReadTest extends App
{
	DataType.setup()
	
	// Reads granite data in background, tracks operation duration
	implicit val exc: ExecutionContext = ThreadPool.executionContext
	val operationStartTime = Instant.now()
	println("Reading data from Granite. This may take a while...")
	val result = ReadGraniteData.asynchronously().waitFor().flatten
	val operationDuration = Instant.now() - operationStartTime
	
	println(s"Operation completed in ${operationDuration.description}")
	println(s"Successfully read ${result.get} responses")
}
