package ch.model.exception

/**
  * Thrown when operator can't be found
  * @author Mikko Hilpinen
  * @since 18.7.2019, v0.1+
  */
class NoSuchOperatorException(message: String) extends RuntimeException(message)
