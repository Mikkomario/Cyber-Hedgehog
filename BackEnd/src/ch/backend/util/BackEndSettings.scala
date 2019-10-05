package ch.backend.util

import ch.util.Settings

/**
  * Reads back-end -specific settings from the settings file
  * @author Mikko Hilpinen
  * @since 22.7.2019, v0.1+
  */
object BackEndSettings
{
	private def data = Settings.data("backend")
	
	/**
	  * @return Duration of how often data should be updated (in minutes)
	  */
	def updateIntervalMinutes = data("updateIntervalMinutes").doubleOr(15)
}
