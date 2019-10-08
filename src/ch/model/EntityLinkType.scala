package ch.model

/**
 * Used for distinguishing between different entity links / relations
 * @author Mikko Hilpinen
 * @since 5.10.2019, v2+
 * @param id This link type's unique identifier
 * @param isContainment Whether in this link type the second entity contains the first
 */
case class EntityLinkType(id: Int, isContainment: Boolean)
