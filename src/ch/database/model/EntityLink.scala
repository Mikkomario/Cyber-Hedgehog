package ch.database.model

import java.time.Instant
import ch.database.Tables
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.factory.LinkedFactory

object EntityLink extends LinkedFactory[ch.model.EntityLink, ch.model.EntityLinkType]
{
	// IMPLEMENTED	------------------
	
	override def childFactory = EntityLinkType
	
	override def apply(model: Model[Constant], child: ch.model.EntityLinkType) = table.requirementDeclaration
		.validate(model).toTry.map { valid =>
		ch.model.EntityLink(valid("id").getInt, valid("origin").getInt, valid("target").getInt, child,
			valid("source").getInt, valid("created").getInstant, valid("deprecatedAfter").instant)
	}
	
	override def table = Tables.entityLink
	
	
	// OTHER	---------------------
	
	/**
	 * Creates a link ready to be inserted
	 * @param originId Id of origin entity
	 * @param targetId Id of target entity
	 * @param typeId Id of link's type
	 * @param sourceId Id of data source
	 * @param created Connection creation time (defaults to current time)
	 * @return A model ready to be inserted
	 */
	def forInsert(originId: Int, targetId: Int, typeId: Int, sourceId: Int, created: Instant = Instant.now()) =
		EntityLink(None, Some(originId), Some(targetId), Some(typeId), Some(sourceId), Some(created))
	
	/**
	 * @param originId Id of link origin entity
	 * @return A model with origin id set
	 */
	def withOriginId(originId: Int) = EntityLink(originId = Some(originId))
}

/**
 * Used for interacting with entity link DB data
 * @author Mikko Hilpinen
 * @since 5.10.2019, v2+
 */
case class EntityLink(id: Option[Int] = None, originId: Option[Int] = None, targetId: Option[Int] = None,
					  linkTypeId: Option[Int] = None, sourceId: Option[Int] = None, created: Option[Instant] = None,
					  deprecatedAfter: Option[Instant] = None) extends StorableWithFactory[ch.model.EntityLink]
{
	// IMPLEMENTED	----------------
	
	override def factory = EntityLink
	
	override def valueProperties = Vector("id" -> id, "origin" -> originId,
		"target" -> targetId, "type" -> linkTypeId, "source" -> sourceId, "created" -> created,
		"deprecatedAfter" -> deprecatedAfter)
	
	
	// OTHER	--------------------
	
	/**
	 * @param targetId Id of targeted entity
	 * @return A copy of this model with specified target id
	 */
	def withTargetId(targetId: Int) = copy(targetId = Some(targetId))
}
