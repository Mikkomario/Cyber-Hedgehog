package ch.database

import java.time.Instant

import ch.model.DataSet
import utopia.flow.datastructure.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.model.immutable.access.{ConditionalManyAccess, IntIdAccess, ManyAccess, ManyAccessWithIds, ManyIdAccess}
import utopia.flow.generic.ValueConversions._
import utopia.vault.sql.Extensions._
import utopia.flow.util.CollectionExtensions._
import utopia.vault.sql.{Condition, ConditionElement, Select, Update, Where}

object EntityIds extends ManyIdAccess[Int] with IntIdAccess
{
	// IMPLEMENTED	-------------------
	
	override def table = Tables.entity
	
	
	// OTHER	-----------------------
	
	/**
	 * @param entityTypeId Id of targeted entity type
	 * @param connection DB Connection
	 * @return All entity ids for specified type
	 */
	def forTypeWithId(entityTypeId: Int)(implicit connection: Connection) = apply(model.Entity.withTypeId(
		entityTypeId).toCondition)
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
	
	private def labelConfigurationTable = labelConfigurationFactory.table
	
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
	
	class EntitiesOfType(typeId: Int) extends ConditionalManyAccess[ch.model.Entity](
		factory.withTypeId(typeId).toCondition, factory)
	{
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
		private def defaultSelect = Select(target, table)
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
		 * @param data New data (labelId -> new value)
		 * @param connection DB connection
		 */
		def insert(readId: Int, data: Traversable[(Int, Value)])(implicit connection: Connection) =
		{
			data.foreach { case(labelId, value) => factory.forInsert(readId, labelId, value).insert() }
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
				val update = Update(table, "deprecatedAfter", Instant.now()).get
				val targetRoleIds: Seq[ConditionElement] = linkTypeIds.map { id => id: ConditionElement }
				connection(update + Where(
					factory.withOriginId(originEntityId).withTargetId(targetEntityId).toCondition &&
						table("type").in(targetRoleIds) && notDeprecatedCondition))
			}
		}
	}
}
