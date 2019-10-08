package ch.model

import java.time.Instant

/**
 * Used for connecting two entities together
 * @author Mikko Hilpinen
 * @since 5.10.2019, v2+
 */
case class EntityLink(id: Int, originEntityId: Int, targetEntityId: Int, linkType: EntityLinkType, dataSourceId: Int,
					  since: Instant, deprecatedSince: Option[Instant] = None)
{
	/**
	 * @return Whether this link is currently active
	 */
	def isDeprecated = deprecatedSince.isDefined
}
