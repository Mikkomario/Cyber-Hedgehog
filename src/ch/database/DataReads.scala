package ch.database

import java.time.Instant

import utopia.vault.sql.Extensions._
import utopia.flow.generic.ValueConversions._
import utopia.vault.database.Connection
import utopia.vault.model.immutable.access.ManyAccess
import utopia.vault.sql.{Select, Where}

/**
 * Used for accessing data reads in DB
 * @author Mikko Hilpinen
 * @since 6.10.2019, v2+
 */
object DataReads extends ManyAccess[Int, ch.model.DataRead]
{
	// COMPUTED	------------------------
	
	private def readTimeColumn = table("created")
	
	
	// IMPLEMENTED	--------------------
	
	override protected def idValue(id: Int) = id
	
	override def factory = model.DataRead
	
	
	// OTHER	------------------------
	
	/**
	 * Provides access to data reads for specific entity type
	 * @param typeId Id of targeted entity type
	 * @return Access to data reads for entities with that type
	 */
	def forTypeWithId(typeId: Int) = new ReadsForType(typeId)
	
	/**
	 * @param connection DB Connection
	 * @return Latest data reads, one for each target entity
	 */
	def latestVersions(implicit connection: Connection) = grouped(all)
	
	/**
	 * @param threshold A time threshold
	 * @param connection DB connection
	 * @return Latest data reads (one for each updated target) after the specified time
	 */
	def latestVersionsAfter(threshold: Instant)(implicit connection: Connection) = grouped(
		factory.getMany(timeThresholdCondition(threshold)))
	
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
	
	private def timeThresholdCondition(threshold: Instant) = readTimeColumn > threshold
	
	private def grouped(data: Traversable[ch.model.DataRead]) = data.groupBy { _.targetId }
		.mapValues { _.maxBy { _.dataOriginTime } }.values
	
	
	// NESTED	--------------------
	
	class ReadsForType(typeId: Int)
	{
		// COMPUTED	----------------
		
		private def target = table join Entity.table
		private def defaultSelect = Select(target, table)
		private def baseCondition = model.Entity.withTypeId(typeId).toCondition
		
		/**
		 * @param connection DB connection
		 * @return All data reads from this sub group
		 */
		def get(implicit connection: Connection) = connection(defaultSelect + Where(baseCondition)).parse(factory)
		
		/**
		 * @param connection DB Connection
		 * @return Latest data reads for all entities of this targeted group
		 */
		def latestVersions(implicit connection: Connection) = grouped(get)
		
		
		// OTHER	----------------
		
		/**
		 * @param threshold Minimum data read time
		 * @param connection DB Connection
		 * @return Latest data reads for all entities of this targeted group (limited by data read time)
		 */
		def latestVersionsAfter(threshold: Instant)(implicit connection: Connection) =
			grouped(connection(defaultSelect + Where(baseCondition && timeThresholdCondition(threshold))).parse(factory))
	}
}