package ch.granite.model

/**
  * Represents an option in a Granite multiselect field
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  * @param id This option's unique identifier
  * @param field The multiselect or drop down field that contains this option
  * @param graniteId The id of this option in Granite system
  */
case class SelectOption(id: Int, field: Field, graniteId: Int)
