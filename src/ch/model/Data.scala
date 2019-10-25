package ch.model

import java.time.Instant

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
case class Data(id: Int, read: DataRead, label: EntityLabel, value: Value, deprecatedAfter: Option[Instant] = None)
{
	/**
	  * @return The identifier of the entity this data concerns
	  */
	def targetId = read.targetId
	
	/**
	 * @return Whether this data has been deprecated (meaning that there is more recent data available)
	 */
	def isDeprecated = deprecatedAfter.isDefined
}