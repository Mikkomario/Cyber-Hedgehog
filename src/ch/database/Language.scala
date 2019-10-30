package ch.database

import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.immutable.access.SingleAccess

/**
 * Access point for individual languages
 * @author Mikko Hilpinen
 * @since 30.10.2019, v3+
 */
object Language extends SingleAccess[Int, ch.model.Language]
{
	// IMPLEMENTED	-----------------------
	
	override protected def idValue(id: Int) = id
	
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
