package ch.database

import utopia.vault.nosql.access.ManyIntIdAccess

object EntityTypeIds extends ManyIntIdAccess
{
	// IMPLEMENTED	-----------------
	
	override def target = table
	
	override def globalCondition = None
	
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
