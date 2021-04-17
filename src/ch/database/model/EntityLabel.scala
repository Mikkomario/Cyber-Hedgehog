package ch.database.model

import ch.database.Tables
import utopia.vault.model.immutable.{Row, StorableWithFactory}
import utopia.vault.nosql.factory.FromRowFactory
import utopia.flow.generic.ValueConversions._
import utopia.vault.sql.JoinType

object EntityLabel extends FromRowFactory[ch.model.EntityLabel]
{
	// IMPLEMENTED	------------------------
	
	override def table = Tables.entityLabel
	
	override def joinType = JoinType.Left
	
	override def apply(row: Row) = table.requirementDeclaration.validate(row(table)).toTry
		.map { model =>
			val configuration = row.columnData.get(EntityLabelConfiguration.table).flatMap {
				EntityLabelConfiguration.apply(_).toOption }
			ch.model.EntityLabel(model("id").getInt, model("targetType").getInt, configuration)
		}
	
	override def joinedTables = EntityLabelConfiguration.tables
	
	
	// OTHER	-----------------------------
	
	/**
	 * @param entityTypeId Id of targeted entity type
	 * @return A model with only target type set
	 */
	def withTargetTypeId(entityTypeId: Int) = EntityLabel(targetTypeId = Some(entityTypeId))
}

/**
 * Used for interacting with entity label data
 * @author Mikko Hilpinen
 * @since 5.10.2019, v2+
 */
case class EntityLabel(id: Option[Int] = None, targetTypeId: Option[Int] = None) extends StorableWithFactory[ch.model.EntityLabel]
{
	override def factory = EntityLabel
	
	override def valueProperties = Vector("id" -> id, "targetType" -> targetTypeId)
}
