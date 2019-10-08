package ch.model

/**
 * Represents a tracked entity, like a company or a contact
 * @author Mikko Hilpinen
 * @since 5.10.2019, v2+
 * @param id Unique id of this entity
 * @param typeId Id of this entity's type
 * @param sourceId Id of the originating source of this entity
 */
case class Entity(id: Int, typeId: Int, sourceId: Int)