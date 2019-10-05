package ch.model.exception

/**
  * Thrown when required settings are missing or malformed
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
class InvalidSettingsException(message: String) extends RuntimeException(message)
