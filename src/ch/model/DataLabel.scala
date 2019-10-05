package ch.model

/**
  * Represents a field for company- or other kind of data data
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  * @param id This label's unique identifier
  * @param dataType The data type for this label's values
  * @param isIdentifier Whether this label's value is considered to be a company identifier (default = false)
  * @param isEmail Whether this label's value represents an email address (default = false)
  */
case class DataLabel(id: Int, dataType: DataType, isIdentifier: Boolean = false, isEmail: Boolean = false)
