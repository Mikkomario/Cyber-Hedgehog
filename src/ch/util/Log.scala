package ch.util

import java.io.{BufferedWriter, FileWriter, PrintWriter}
import java.nio.file.{Files, Paths}
import java.time.{LocalDate, LocalDateTime}

import utopia.flow.util.AutoClose._
import utopia.flow.async.Volatile

/**
  * Used for logging various errors
  * @author Mikko Hilpinen
  * @since 13.7.2019, v0.1+
  */
object Log
{
	// ATTRIBUTES	-----------------
	
	private val writer = new Volatile[Writer](new Writer())
	
	
	// OPERATORS	-----------------
	
	/**
	  * Writes an error to the log
	  * @param error An error
	  * @param additionalMessage Additional error message
	  */
	def apply(error: Throwable, additionalMessage: String = ""): Unit = apply(Some(error), additionalMessage)
	
	/**
	  * Logs a warning message
	  * @param message A message
	  */
	def warning(message: String) = apply(None, message)
	
	private def apply(error: Option[Throwable], additionalMessage: String): Unit =
	{
		// Replaces the writer when it becomes invalid (day changes)
		if (!writer.get.isValid)
			writer.set(new Writer)
		
		writer.lock { _{ w => w.println(); w.println(LocalDateTime.now())
			if (additionalMessage.nonEmpty) w.println(additionalMessage); error.foreach { _.printStackTrace(w) } } }
		
		// May print errors to system err
		if (Settings.Logging.allowPrint)
		{
			if (additionalMessage.nonEmpty)
				System.err.println(additionalMessage)
			error.foreach { _.printStackTrace() }
		}
	}
	
	
	// NESTED CLASSES	-------------
	
	private class Writer
	{
		// ATTRIBUTES	-------------
		
		private val startDate = LocalDate.now()
		private val path = Paths.get(s"${Settings.Logging.directory}/log-$startDate.txt")
		
		
		// INITIAL CODE	-------------
		
		Files.createDirectories(path.getParent)
		
		
		// COMPUTED	-----------------
		
		def isValid = LocalDate.now() == startDate
		
		
		// OPERATORS	-------------
		
		def apply(operation: PrintWriter => Unit) =
		{
			new PrintWriter(new BufferedWriter(new FileWriter(path.toFile, true))).tryConsume(operation)
		}
	}
}
