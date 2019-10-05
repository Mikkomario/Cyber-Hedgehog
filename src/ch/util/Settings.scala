package ch.util

import java.nio.file.Paths

import utopia.flow.datastructure.immutable.Model
import utopia.flow.util.CollectionExtensions._
import utopia.flow.parse.JSONReader


/**
  * Contains settings used in this project (read from .json file)
  * @author Mikko Hilpinen
  * @since 13.7.2019, v0.1+
  */
object Settings
{
	// ATTRIBUTES	----------------
	
	private val path = Paths.get("resources/settings.json")
	
	/**
	  * Settings file data in a model format
	  */
	lazy val data =
	{
		val readTry = JSONReader(path.toFile)
		readTry.failure.foreach { e =>
			// Cannot use Log at this time because it hasn't been initialized
			println("Settings initialization failed.")
			e.printStackTrace()
		}
		readTry.map { _.getModel }.getOrElse(Model.empty)
	}
	
	
	// NESTED CLASSES	------------
	
	object Logging
	{
		/**
		  * @return The directory where data is logged
		  */
		def directory = Paths.get(data("logging")("directory").stringOr("log"))
		
		/**
		  * @return Whether errors should be printed to System.err
		  */
		def allowPrint = data("logging")("allowPrint").getBoolean
	}
	
	object Database
	{
		/**
		  * @return Password used when connecting to DB
		  */
		def password = data("database")("password").getString
		/**
		  * @return Specific driver used. None if left empty.
		  */
		def driver = data("database")("driver").string
	}
}
