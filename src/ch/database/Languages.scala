package ch.database

import utopia.vault.nosql.access.ManyRowModelAccess

/**
 * Access point for multiple languages
 * @author Mikko Hilpinen
 * @since 30.10.2019, v3+
 */
object Languages extends ManyRowModelAccess[ch.model.Language]
{
	// IMPLEMENTED	--------------------
	
	override def globalCondition = None
	
	override def factory = model.Language
}
