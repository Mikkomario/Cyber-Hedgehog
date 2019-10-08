package ch.database

import utopia.vault.model.immutable.access.{IntIdAccess, ManyAccessWithIds, ManyIdAccess}

object EntityTypeIds extends ManyIdAccess[Int] with IntIdAccess
{
	// IMPLEMENTED	-----------------
	
	override def table = Tables.entityType
}

/**
 * Used for accessing multiple entity types
 * @author Mikko Hilpinen
 * @since 7.10.2019, v2+
 */
object EntityTypes
{
	/**
	 * @return Access point to all entity ids
	 */
	def ids = EntityTypeIds
}
