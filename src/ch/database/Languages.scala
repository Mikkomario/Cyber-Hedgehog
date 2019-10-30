package ch.database

import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.access.ManyAccess

/**
 * Access point for multiple languages
 * @author Mikko Hilpinen
 * @since 30.10.2019, v3+
 */
object Languages extends ManyAccess[Int, ch.model.Language]
{
	// IMPLEMENTED	--------------------
	
	override protected def idValue(id: Int) = id
	
	override def factory = model.Language
}
