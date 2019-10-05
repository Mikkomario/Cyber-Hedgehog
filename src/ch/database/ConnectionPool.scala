package ch.database

import utopia.flow.util.TimeExtensions._

/**
  * Provides database connections for classes under this project
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
object ConnectionPool extends utopia.vault.database.ConnectionPool(100, 4, 30.seconds)
