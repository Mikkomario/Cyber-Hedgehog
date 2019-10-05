package ch.model.exception

/**
  * Thrown when authentication fails
  * @author Mikko Hilpinen
  * @since 14.7.2019, v0.1+
  */
class AuthenticationFailedException(message: String) extends RuntimeException(message)
