package ch.database

import utopia.vault.database.Connection
import utopia.vault.nosql.access.SingleRowModelAccess

/**
 * Access point for individual languages
 * @author Mikko Hilpinen
 * @since 30.10.2019, v3+
 */
object Language extends SingleRowModelAccess[ch.model.Language]
{
	// IMPLEMENTED	-----------------------
	
	override def globalCondition = None
	
	override def factory = model.Language
	
	
	// OTHER	---------------------------
	
	/**
	 * Finds a language for an ISO code
	 * @param isoCode ISO standard code for searched language
	 * @param connection DB Connection
	 * @return Language with specified code. None if not found
	 */
	def forCode(isoCode: String)(implicit connection: Connection) = find(factory.withCode(isoCode).toCondition)
}
