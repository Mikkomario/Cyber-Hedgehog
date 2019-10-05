package ch.model

import utopia.flow.datastructure.immutable.Value

/**
  * Represents a single label's value at a certain read event
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  * @param id This data's unique identifier
  * @param read Information about this data's read event
  * @param label Label that describes this data's role
  * @param value Value associated with this data
  */
case class Data(id: Int, read: DataRead, label: DataLabel, value: Value)
{
	/**
	  * @return The identifier of the entity this data concerns
	  */
	def targetId = read.targetId
}