package ch.model

import java.time.Instant

/**
 * Used for specifying current label settings
 * @author Mikko Hilpinen
 * @since 5.10.2019, v2+
 * @param id This configuration's unique id
 * @param labelId Id of the label affected by this configuration
 * @param dataType The specified data type for the label
 * @param isIdentifier whether the label should be considered an identifier
 * @param isEmail Whether the label is configured to represent an email
 * @param created Time when this configuration was made
 * @param deprecatedSince Time after this configuration became deprecated
 */
case class DataLabelConfiguration(id: Int, labelId: Int, dataType: DataType, isIdentifier: Boolean = false,
								  isEmail: Boolean = false, created: Instant = Instant.now(),
								  deprecatedSince: Option[Instant] = None)
{
	def isDeprecated = deprecatedSince.isDefined
}
