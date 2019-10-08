package ch.model

/**
 * Represents a link between an entity label and a label group
 * @author Mikko Hilpinen
 * @since 21.8.2019, v1.1+
 * @param id This link's unique id
 * @param groupId Id of linked group
 * @param labelId Id of linked label
 * @param orderIndex Ordering index for this link, if specified
 */
case class EntityLabelGroupConnection(id: Int, groupId: Int, labelId: Int, orderIndex: Option[Int])
