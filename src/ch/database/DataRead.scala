package ch.database

import utopia.vault.database.Connection
import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.access.{SingleIdModelAccess, SingleModelAccessById}
import utopia.vault.sql.{MaxBy, Select, Where}

/**
 * Used for accessing individual data reads in DB
 * @author Mikko Hilpinen
 * @since 6.10.2019, v2+
 */
object DataRead extends SingleModelAccessById[ch.model.DataRead, Int]
{
	// COMPUTED	--------------------
	
	private def readTimeColumn = table("created")
	
	/**
	 * @param connection DB connection
	 * @return The latest data read
	 */
	def latest(implicit connection: Connection) = factory.getMax(readTimeColumn)
	
	/**
	 * @param typeId Targeted entity type's id
	 * @return An access point to data read for specified entity type
	 */
	def forTypeWithId(typeId: Int) = new ReadOfType(typeId)
	
	/**
	 * @param sourceId Data source's id
	 * @return An access point to data read from specific data source
	 */
	def fromSourceWithId(sourceId: Int) = new ReadFromSource(sourceId)
	
	
	// IMPLEMENTED	----------------
	
	override def idToValue(id: Int) = id
	
	override def factory = model.DataRead
	
	override def apply(id: Int) = new SingleDataRead(id)
	
	
	// NESTED	------------------------
	
	class SingleDataRead(id: Int) extends SingleIdModelAccess[ch.model.DataRead](id, factory)
	{
		/**
		 * Finds all target data that was read at this read event
		 * @param connection DB connection
		 * @return All read data
		 */
		def data(implicit connection: Connection) = Entities.data.forReadWithId(id)
	}
	
	class ReadOfType(val typeId: Int)
	{
		private def target = table join Entity.table
		private def defaultSelect = Select(target, table)
		private def baseCondition = model.Entity.withTypeId(typeId).toCondition
		
		/**
		 * @param connection DB connection
		 * @return The latest data read from this sub group
		 */
		def latest(implicit connection: Connection) = connection(defaultSelect + Where(baseCondition) +
			MaxBy(readTimeColumn)).parseSingle(factory)
	}
	
	class ReadFromSource(val sourceId: Int)
	{
		/**
		 * @param connection DB connection
		 * @return The latest data read for this specific source
		 */
		def latest(implicit connection: Connection) = factory.getMax(readTimeColumn,
			factory.withSourceId(sourceId).toCondition)
	}
	
	class ReadForEntity(val entityId: Int)
	{
		/**
		 * @param connection DB connection
		 * @return The latest data read for specified entity
		 */
		def latest(implicit connection: Connection) = factory.getMax(readTimeColumn,
			factory.withTargetId(entityId).toCondition)
	}
}
