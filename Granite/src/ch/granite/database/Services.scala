package ch.granite.database

import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.access.{IntIdAccess, ManyAccessWithIds, ManyIdAccess}

object ServiceIds extends ManyIdAccess[Int] with IntIdAccess
{
	override def table = Tables.service
}

/**
 * Used for accessing granite services in DB
 * @author Mikko Hilpinen
 * @since 6.10.2019, v2+
 */
object Services extends ManyAccessWithIds[Int, ch.granite.model.Service, ServiceIds.type]
{
	// IMPLEMENTED	--------------------
	
	override def ids = ServiceIds
	
	override protected def idValue(id: Int) = id
	
	override def factory = model.Service
}
