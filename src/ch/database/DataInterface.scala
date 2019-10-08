package ch.database

import java.time.Instant

import utopia.vault.sql.Extensions._
import utopia.flow.generic.ValueConversions._
import ch.database.model.{Data, DataFactory, DataLabel, DataLabelFactory, DataReadOld, DataReadFactory}
import ch.model.DataSet
import utopia.flow.datastructure.immutable.Value
import utopia.flow.parse.JSONReader
import utopia.vault.database.Connection
import utopia.vault.sql.{Limit, MaxBy, Select, Where}

/**
  * Common trait for specific data DB interface objects
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
@deprecated("Replaced with Entity", "v2")
trait DataInterface[DataModel <: Data[DataModel], ReadModel <: DataReadOld, LabelModel <: DataLabel]
{
	// ABSTRACT	-------------------
	
	/**
	  * @return Factory used for reading data
	  */
	def dataFactory: DataFactory[DataModel]
	/**
	  * @return Factory used for reading data read events
	  */
	def readFactory: DataReadFactory[ReadModel]
	/**
	  * @return Factory used for reading data labels
	  */
	def labelFactory: DataLabelFactory[LabelModel]
	
	
	// COMPUTED	-------------------
	
	private def readTable = readFactory.table
	private def dataTable = dataFactory.table
	private def labelTable = labelFactory.table
	
	private def readTime = readTable("created")
	private def dataValue = dataTable("value")
	
	
	// OTHER	-------------------
	
	/**
	  * @param connection DB connection
	  * @return All associated identifier labels in DB
	  */
	@deprecated("Replaced with Entity.Labels.forTypeWithId(...).identifiers", "v2")
	def identifierLabels(implicit connection: Connection) = labelFactory.identifier.searchMany()
	
	/**
	  * @param connection DB connection
	  * @return Data labels that represent email addresses
	  */
	@deprecated("Replaced with Entity.Labels.forTypeWithId(...).emails", "v2")
	def emailLabels(implicit connection: Connection) = labelFactory.getMany(labelFactory.email.toCondition)
	
	/**
	  * Finds target id for a specified identifier
	  * @param identifierLabelId The identifier label's id
	  * @param identifierValue The searched identifier value
	  * @param connection DB connection
	  * @return Target id for the match. None if no such target was found
	  */
	@deprecated("Replaced with Entity.id.forIdentifier(...)", "v2")
	def idForIdentifier(identifierLabelId: Int, identifierValue: Value)(implicit connection: Connection) =
	{
		connection(Select(readTable join dataTable, readTable(readFactory.targetPropertyName)) +
			Where(dataFactory.withLabelId(identifierLabelId).withValue(identifierValue)) + Limit(1)).firstValue.int
	}
	
	/**
	  * Inserts new read data to DB
	  * @param sourceId Data origin source id
	  * @param targetId Data target id
	  * @param readTime The time when data was read
	  * @param connection DB connection
	  * @return The new data read instance
	  */
	@deprecated("Replaced with DataRead.insert(...)", "v2")
	def insertRead(sourceId: Int, targetId: Int, dataOriginTime: Instant, readTime: Instant = Instant.now())
				  (implicit connection: Connection) =
	{
		val id = readFactory.forInsert(sourceId, targetId, dataOriginTime, readTime).insert().getInt
		ch.model.DataRead(id, sourceId, targetId, dataOriginTime, readTime)
	}
	
	/**
	  * Finds the latest data read event from DB
	  * @param connection DB connection
	  * @return The latest data read event. None if no data was read yet.
	  */
	@deprecated("Replaced with DataReads.latest", "v2")
	def lastRead(implicit connection: Connection) = readFactory.getMax("created")
	
	/**
	  * Finds the last data read event for the specified read target
	  * @param targetId Target id
	  * @param after A minimum threshold for including data reads (optional)
	  * @param connection DB connection
	  * @return Latest read for the specified target (after threshold)
	  */
	@deprecated("Replaced with Entity(id).latestRead", "v2")
	def lastReadForTargetId(targetId: Int, after: Option[Instant] = None)(implicit connection: Connection) =
	{
		val targetCondition = readFactory.withTargetId(targetId).toCondition
		val where = after.map { timeThreshold => targetCondition && (readTime > timeThreshold) }.getOrElse(targetCondition)
		
		readFactory.getMax(readTime, where)
	}
	
	/**
	  * @param threshold A time threshold
	  * @param connection DB connection
	  * @return Latest data reads (one for each updated target) after the specified time
	  */
	@deprecated("Replaced with DataReads.latestVersionsAfter(...)", "v2")
	def lastReadsAfter(threshold: Instant)(implicit connection: Connection) =
	{
		readFactory.getMany(readTime > threshold).groupBy { _.targetId }.mapValues { _.maxBy { _.dataOriginTime } }.values
	}
	
	/**
	  * @param connection DB connection
	  * @return Latest reads for each target (one read per target)
	  */
	@deprecated("Replaced with DataReads.latestVersions", "v2")
	def lastReads(implicit connection: Connection) =
		readFactory.getAll().groupBy { _.targetId }.mapValues { _.maxBy { _.dataOriginTime } }.values
	
	/**
	  * Inserts new target data to DB
	  * @param readId The data read instance's id
	  * @param data New data (labelId -> new value)
	  * @param connection DB connection
	  */
	@deprecated("Replaced with Entity.Data.insert(...)", "v2")
	def insertData(readId: Int, data: Traversable[(Int, Value)])(implicit connection: Connection) =
	{
		data.foreach { case(labelId, value) => dataFactory.forInsert(readId, labelId, value).insert() }
	}
	
	/**
	  * Finds all target data that was read at certain read event
	  * @param readId Read event id
	  * @param connection DB connection
	  * @return All read data
	  */
	@deprecated("Replaced with DataRead(id).data", "v2")
	def readData(readId: Int)(implicit connection: Connection) =
	{
		val data = dataFactory.getMany(dataFactory.withReadId(readId).toCondition)
		DataSet(data.map { d => d.label -> d.value }.toSet)
	}
	
	/**
	  * Finds the latest email address for the specified target
	  * @param targetId Target's id
	  * @param connection DB connection
	  * @return Latest email for specified target
	  */
	@deprecated("Replaced with Entity(id).email", "v2")
	def emailForId(targetId: Int)(implicit connection: Connection) =
	{
		connection(Select(dataTable join labelTable join readTable, dataValue) +
			Where(readFactory.withTargetId(targetId).toCondition && labelFactory.email.toCondition) + MaxBy(readTime))
			.firstValue.string.flatMap { JSONReader(_).toOption }.flatMap { _.string }
	}
}
