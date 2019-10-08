package ch.granite.model

/**
 * Represents a service in the Granite platform
 * @author Mikko Hilpinen
 * @since 6.10.2019, v2+
 * @param id Unique id of this service
 * @param graniteId This service's id in Granite system
 */
case class Service(id: Int, graniteId: Int)
