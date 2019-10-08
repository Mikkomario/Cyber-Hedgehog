package ch.database

import java.time.Instant

import utopia.vault.database.Connection
import utopia.vault.model.immutable.access.{ItemAccess, SingleAccess}
import utopia.flow.generic.ValueConversions._

/**
 * Used for accessing individual data reads in DB
 * @author Mikko Hilpinen
 * @since 6.10.2019, v2+
 */
object DataRead extends SingleAccess[Int, ch.model.DataRead]
{
	// IMPLEMENTED	----------------
	
	override protected def idValue(id: Int) = id
	
	override def factory = model.DataRead
	
	override def apply(id: Int) = new SingleDataRead(id)
	
	
	// OTHER	--------------------
	
	/**
	 * Inserts new read data to DB
	 * @param sourceId Data origin source id
	 * @param targetId Data target id
	 * @param readTime The time when data was read
	 * @param connection DB connection
	 * @return The new data read instance
	 */
	def insert(sourceId: Int, targetId: Int, dataOriginTime: Instant, readTime: Instant = Instant.now())
			  (implicit connection: Connection) =
	{
		val id = factory.forInsert(sourceId, targetId, dataOriginTime, readTime).insert().getInt
		ch.model.DataRead(id, sourceId, targetId, dataOriginTime, readTime)
	}
	
	
	// NESTED	------------------------
	
	class SingleDataRead(id: Int) extends ItemAccess[ch.model.DataRead](id, factory)
	{
		/**
		 * Finds all target data that was read at this read event
		 * @param connection DB connection
		 * @return All read data
		 */
		def data(implicit connection: Connection) = Entity.Data.forReadWithId(id)
	}
}
