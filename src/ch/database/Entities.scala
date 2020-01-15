package ch.database

import java.time.Instant

import ch.model.DataSet
import utopia.flow.datastructure.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.model.immutable.access.{ConditionalManyAccess, IntIdAccess, ManyAccess, ManyAccessWithIds, ManyIdAccess}
import utopia.flow.generic.ValueConversions._
import utopia.vault.sql.Extensions._
import utopia.flow.util.CollectionExtensions._
import utopia.vault.sql.{Condition, ConditionElement, Select, SelectAll, Update, Where}

import scala.collection.immutable.HashMap

object EntityIds extends ManyIdAccess[Int] with IntIdAccess
{
	// COMPUTED	-----------------------
	
	private def readTable = model.DataRead.table
	private def readTimeColumn = readTable("created")
	
	
	// IMPLEMENTED	-------------------
	
	override def table = Tables.entity
	
	
	// OTHER	-----------------------
	
	/**
	 * @param entityTypeId Id of targeted entity type
	 * @param connection DB Connection
	 * @return All entity ids for specified type
	 */
	def forTypeWithId(entityTypeId: Int)(implicit connection: Connection) = new EntityIdsOfType(entityTypeId)
	
	
	// NESTED	-----------------------
	
	class EntityIdsOfType(val typeId: Int)
	{
		// COMPUTED	-------------------
		
		private def condition = model.Entity.withTypeId(typeId).toCondition
		
		/**
		 * @param connection DB Connection
		 * @return ids of entities of specified type
		 */
		def get(implicit connection: Connection) = apply(condition)
		
		
		// OTHER	-------------------
		
		/**
		 * @param threshold Time threshold
		 * @param connection DB Connection
		 * @return Ids for entities whose data has been updated since specified time threshold
		 */
		def updatedAfter(threshold: Instant)(implicit connection: Connection) = connection(
			Select.index(table join readTable, table) + Where(readTimeColumn >= threshold)).rowIntValues.toSet
	}
}

/**
 * Used for accessing DB entity data multiple instances at a time
 * @author Mikko Hilpinen
 * @since 6.10.2019, v2+
 */
object Entities extends ManyAccessWithIds[Int, ch.model.Entity, EntityIds.type]
{
	// COMPUTED	-------------------------
	
	private def labelConfigurationFactory = model.EntityLabelConfiguration
	private def readFactory = model.DataRead
	
	private def labelConfigurationTable = labelConfigurationFactory.table
	private def readTable = readFactory.table
	
	private def nonDeprecatedCondition = labelConfigurationTable("deprecatedAfter").isNull
	
	/**
	 * @return Access point to entity labels
	 */
	def labels = Labels
	
	/**
	 * @return Access point to entity data
	 */
	def data = Data
	
	/**
	 * @return Access point to entity links
	 */
	def links = Links
	
	
	// IMPLEMENTED	---------------------
	
	override def ids = EntityIds
	
	override protected def idValue(id: Int) = id
	
	override def factory = model.Entity
	
	
	// OTHER	-------------------------
	
	/**
	 * Provides access to a sub group of entities
	 * @param typeId Id of targeted entity type
	 * @return Access to entities of specific type only
	 */
	def ofTypeWithId(typeId: Int) = new EntitiesOfType(typeId)
	
	/**
	 * Reads entities that work within specified parent entities
	 * @param parentEntityIds Ids of parent entities
	 * @param since Limit results by specifying minimum time after which the link must have been made
	 *              (optional, default = None = No limit)
	 * @param connection DB Connection
	 * @return Entities that work (or were added) within entities with specified ids
	 */
	def withinOther(parentEntityIds: Set[Int], since: Option[Instant] = None)
				   (implicit connection: Connection): Vector[ch.model.Entity] = withinOther(parentEntityIds, since, None)
	
	private def withinOther(parentEntityIds: Set[Int], since: Option[Instant], additionalCondition: Option[Condition])
				   (implicit connection: Connection): Vector[ch.model.Entity] =
	{
		if (parentEntityIds.nonEmpty)
		{
			val baseCondition = (model.EntityLink.table("target") in parentEntityIds.map { id => id: ConditionElement }) &&
				model.EntityLinkType.containment.toCondition
			val withTimeCondition = since.map { time => baseCondition && (model.EntityLink.table("created") > time) }
				.getOrElse(baseCondition)
			val finalCondition = additionalCondition.map { withTimeCondition && _ }.getOrElse(withTimeCondition)
			
			connection(Select((model.EntityLink.table join model.EntityLinkType.table).joinFrom(model.EntityLink.table("origin")),
				table) + Where(finalCondition)).parse(factory).distinctBy { _.id }
		}
		else
			Vector()
	}
	
	
	// NESTED	--------------------------
	
	class EntitiesOfType(typeId: Int) extends ConditionalManyAccess[ch.model.Entity]
	{
		override def condition = factory.withTypeId(typeId).toCondition
		
		override def factory = Entities.factory
		
		/**
		 * Reads entities that work within specified parent entities
		 * @param parentEntityIds Ids of parent entities
		 * @param since Limit results by specifying minimum time after which the link must have been made
		 *              (optional, default = None = No limit)
		 * @param connection DB Connection
		 * @return Entities that work (or were added) within entities with specified ids
		 */
		def withinOther(parentEntityIds: Set[Int], since: Option[Instant] = None)
					   (implicit connection: Connection): Vector[ch.model.Entity] =
			Entities.withinOther(parentEntityIds, since, Some(condition))
	}
	
	/**
	 * Used for reading data for multiple labels in DB
	 */
	object Labels
	{
		// COMPUTED	--------------------
		
		private def factory = model.EntityLabel
		private def table = factory.table
		private def target = table join labelConfigurationTable
		private def defaultSelect = SelectAll(target)
		private def defaultCondition = nonDeprecatedCondition
		
		/**
		 * @param connection DB Connection
		 * @return All labels in DB (with their latest configurations only)
		 */
		def all(implicit connection: Connection) = connection(defaultSelect +
			Where(defaultCondition)).parse(factory)
		
		
		// OTHER	--------------------
		
		/**
		 * @param typeId Id of targeted entity type
		 * @return Access to labels for the specified entity type
		 */
		def forTypeWithId(typeId: Int) = new LabelsForType(typeId)
		
		
		// NESTED	--------------------
		
		class LabelsForType(typeId: Int)
		{
			// COMPUTED	----------------
			
			private def defaultCondition = Labels.defaultCondition && factory.withTargetTypeId(typeId).toCondition
			
			/**
			 * Reads label data
			 * @param connection DB connection
			 * @return All labels for this specified type
			 */
			def get(implicit connection: Connection) = connection(defaultSelect +
				Where(defaultCondition)).parse(factory)
			
			/**
			 * @param connection DB Connection
			 * @return all labels for this type that contain identifiers
			 */
			def identifiers(implicit connection: Connection) = find(
				labelConfigurationFactory.identifier.toCondition)
			
			/**
			 * @param connection DB Connection
			 * @return All labels for this type that contain emails
			 */
			def emails(implicit connection: Connection) = find(
				labelConfigurationFactory.email.toCondition)
			
			private def find(additionalCondition: Condition)(implicit connection: Connection) =
				connection(defaultSelect + Where(defaultCondition && additionalCondition)).parse(factory)
		}
	}
	
	object Data extends ManyAccess[Int, ch.model.Data]
	{
		// COMPUTED	---------------------
		
		private def deprecatedColumn = table("deprecatedAfter")
		
		private def nonDeprecatedCondition = deprecatedColumn.isNull
		
		
		// IMPLEMENTED	-----------------
		
		override protected def idValue(id: Int) = id
		
		override def factory = model.EntityData
		
		
		// OTHER	---------------------
		
		/**
		 * Finds all target data that was read at certain read event
		 * @param readId Read event id
		 * @param connection DB connection
		 * @return All read data
		 */
		def forReadWithId(readId: Int)(implicit connection: Connection) =
		{
			val data = find(factory.withReadId(readId).toCondition)
			DataSet(data.map { d => d.label -> d.value }.toSet)
		}
		
		/**
		 * Inserts new target data to DB
		 * @param readId The data read instance's id
		 * @param readTargetId Id of entity whose data is updated
		 * @param data New data (labelId -> new value)
		 * @param connection DB connection
		 */
		def insert(readId: Int, readTargetId: Int, data: Traversable[(Int, Value)])(implicit connection: Connection) =
		{
			// Will not insert empty values
			val nonEmptyData = data.filter { _._2.isDefined }
			
			// Deprecates older versions of data and inserts the new version
			val target = table join readTable
			val update = Update(target, HashMap(table -> factory.withDeprecatedAfter(Instant.now()).toModel.withoutEmptyValues))
			val baseCondition = readFactory.withTargetId(readTargetId).toCondition && nonDeprecatedCondition
			
			nonEmptyData.foreach { case (labelId, value) =>
				connection(update + Where(baseCondition && factory.withLabelId(labelId).toCondition))
				model.EntityData.forInsert(readId, labelId, value).insert()
			}
		}
	}
	
	object Links extends ManyAccess[Int, ch.model.EntityLink]
	{
		// COMPUTED	-----------------------
		
		private def deprecatedColumn = table("deprecatedAfter")
		private def notDeprecatedCondition = deprecatedColumn.isNull
		
		
		// IMPLEMENTED	-------------------
		
		override protected def idValue(id: Int) = id
		override def factory = model.EntityLink
		
		
		// OTHER	-----------------------
		
		/**
		 * Inserts a link between two entities
		 * @param originEntityId Id of link origin
		 * @param targetEntityId Id of link target
		 * @param sourceId Id of link data source
		 * @param linkTypeId Id of link type
		 * @param connection DB connection
		 * @return Id of inserted link
		 */
		def insert(originEntityId: Int, targetEntityId: Int, sourceId: Int, linkTypeId: Int)
				  (implicit connection: Connection) = factory.forInsert(originEntityId, targetEntityId,
			linkTypeId, sourceId).insert().getInt
		
		/**
		 * Links two entities together
		 * @param originEntityId Id of link origin
		 * @param targetEntityId Id of link target
		 * @param sourceId Id of source of this link
		 * @param linkTypeIds Type of relations registered for the entities
		 * @param connection DB Connection
		 */
		def addBetween(originEntityId: Int, targetEntityId: Int, sourceId: Int, linkTypeIds: Traversable[Int])
					  (implicit connection: Connection) = linkTypeIds.foreach { typeId =>
			factory.forInsert(originEntityId, targetEntityId, typeId, sourceId).insert() }
		
		/**
		 * Removes (deprecates) certain links between two entities
		 * @param originEntityId Id of link origin
		 * @param targetEntityId Id of link target
		 * @param linkTypeIds Ids of relation / link types that will be deprecated / removed
		 * @param connection DB Connection
		 */
		def removeBetween(originEntityId: Int, targetEntityId: Int, linkTypeIds: Seq[Int])(implicit connection: Connection): Unit =
		{
			if (linkTypeIds.nonEmpty)
			{
				val update = Update(table, "deprecatedAfter", Instant.now())
				val targetRoleIds: Seq[ConditionElement] = linkTypeIds.map { id => id: ConditionElement }
				connection(update + Where(
					factory.withOriginId(originEntityId).withTargetId(targetEntityId).toCondition &&
						table("type").in(targetRoleIds) && notDeprecatedCondition))
			}
		}
	}
}
