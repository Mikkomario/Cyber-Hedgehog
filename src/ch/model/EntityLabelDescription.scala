package ch.model

import java.time.Instant

/**
 * Used for providing human-readable descriptions for entity labels
 * @author Mikko Hilpinen
 * @since 25.10.2019, v3+
 * @param id Unique id of this label description
 * @param name Name of the targeted label
 * @param description Description for the targeted label
 * @param languageCode ISO code of the language in which name and description are given
 * @param created Time of creation for this description
 * @param deprecatedAfter Time after which this description became deprecated (was replaced with a new version)
 */
case class EntityLabelDescription(id: Int, labelId: Int, name: String, description: Option[String], languageCode: String,
								  created: Instant, deprecatedAfter: Option[Instant])
{
	/**
	 * @return Whether this description should be considered deprecated
	 */
	def isDeprecated = deprecatedAfter.isDefined
}
