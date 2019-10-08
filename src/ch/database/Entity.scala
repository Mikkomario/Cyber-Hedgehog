package ch.database

import java.time.Instant

import ch.model.DataSet
import utopia.flow.datastructure.immutable.Value
import utopia.vault.model.immutable.access.{IntIdAccess, ItemAccess, ManyAccess, SingleAccessWithIds, SingleIdAccess}
import utopia.flow.generic.ValueConversions._
import utopia.flow.parse.JSONReader
import utopia.vault.sql.Extensions._
import utopia.vault.database.Connection
import utopia.vault.sql.{Condition, ConditionElement, Limit, MaxBy, Select, Update, Where}

object EntityId extends SingleIdAccess[Int] with IntIdAccess
{
	// COMPUTED	-------------------------
	
	private def readFactory = model.DataRead
	private def dataFactory = model.EntityData
	private def readTable = readFactory.table
	private def dataTable = dataFactory.table
	
	
	// IMPLEMENTED	---------------------
	
	override def table = model.Entity.table
	
	
	// OTHER	-------------------------
	
	/**
	 * Finds entity id for a specified identifier
	 * @param identifierLabelId The identifier label's id
	 * @param identifierValue The searched identifier value
	 * @param connection DB connection
	 * @return Target id for the match. None if no such target was found
	 */
	def forIdentifier(identifierLabelId: Int, identifierValue: Value)(implicit connection: Connection) =
	{
		connection(Select(readTable join dataTable, readTable("target")) +
			Where(dataFactory.withLabelId(identifierLabelId).withValue(identifierValue)) + Limit(1)).firstValue.int
	}
}

/**
 * Used for accessing DB entity data
 * @author Mikko Hilpinen
 * @since 5.10.2019, v2+
 */
object Entity extends SingleAccessWithIds[Int, ch.model.Entity, EntityId.type]
{
	// COMPUTED	-------------------------
	
	private def readFactory = model.DataRead
	private def labelConfigurationFactory = model.EntityLabelConfiguration
	
	private def dataTable = model.EntityData.table
	private def readTable = readFactory.table
	private def labelTable = model.EntityLabel.table
	private def labelConfigurationTable = labelConfigurationFactory.table
	
	private def dataValueColumn = dataTable("value")
	private def readTimeColumn = readTable("created")
	
	private def nonDeprecatedCondition = labelConfigurationTable("deprecatedAfter").isNull
	
	/**
	 * @param connection DB Connection
	 * @return All available entity type ids
	 */
	def typeIds(implicit connection: Connection) = Tables.entityType.allIndices.flatMap { _.int }
	
	
	// IMPLEMENTED	---------------------
	
	override protected def idValue(id: Int) = id
	
	override def factory = model.Entity
	
	override def id = EntityId
	
	override def apply(id: Int) = new SingleEntity(id)
	
	
	// OTHER	------------------------
	
	/**
	 * Inserts a new entity to DB
	 * @param typeId Entity type id
	 * @param sourceId Id of source of entity data
	 * @param connection DB connection
	 * @return Inserted entity
	 */
	def insert(typeId: Int, sourceId: Int)(implicit connection: Connection) =
	{
		val id = factory.forInsert(typeId, sourceId).insert().getInt
		ch.model.Entity(id, typeId, sourceId)
	}
	
	private def readTargetCondition(targetEntityId: Int) = readFactory.withTargetId(targetEntityId).toCondition
	
	
	// NESTED	------------------------
	
	class SingleEntity(id: Int) extends ItemAccess[ch.model.Entity](id, factory)
	{
		/**
		 * Reads email for this entity
		 * @param connection DB Connection
		 * @return Email of this entity. None if this entity didn't exist or it doesn't have an email
		 */
		def email(implicit connection: Connection) =
		{
			connection(Select(dataTable join readTable join labelTable join labelConfigurationTable, dataValueColumn) +
				Where(readTargetCondition(id) && nonDeprecatedCondition && labelConfigurationFactory.email.toCondition) +
				MaxBy(readTimeColumn)).firstValue.string.flatMap { JSONReader(_).toOption }.flatMap { _.string }
		}
		
		/**
		 * @param connection DB Connection
		 * @return The latest data read for entity with this id (None if no read could be found)
		 */
		def latestRead(implicit connection: Connection) = readFactory.getMax(readTimeColumn,
			readTargetCondition(id))
		
		/**
		 * @param timeThreshold The point in time before which items are excluded
		 * @param connection DB connection
		 * @return The latest data read for this entity <b>after</b> specified time. None if no data was read after that time.
		 */
		def latestReadAfter(timeThreshold: Instant)(implicit connection: Connection) =
			readFactory.getMax(readTimeColumn, readTargetCondition(id) && readTimeColumn > timeThreshold)
	}
}