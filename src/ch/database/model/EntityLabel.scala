package ch.database.model

import ch.database.Tables
import ch.util.Log
import utopia.vault.model.immutable.{Row, StorableWithFactory}
import utopia.vault.model.immutable.factory.FromRowFactory
import utopia.flow.generic.ValueConversions._

import scala.util.{Failure, Success}

object EntityLabel extends FromRowFactory[ch.model.DataLabel]
{
	// IMPLEMENTED	------------------------
	
	override def table = Tables.entityLabel
	
	override def apply(row: Row) = table.requirementDeclaration.validate(row(table)).toTry match {
		case Success(model) =>
			val configuration = row.columnData.get(EntityLabelConfiguration.table).flatMap {
				EntityLabelConfiguration.apply(_).toOption }
			Some(ch.model.DataLabel(model("id").getInt, model("targetType").getInt, configuration))
			
		case Failure(e) => Log(e, s"Failed to parse EntityLabel from $row"); None
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
case class EntityLabel(id: Option[Int] = None, targetTypeId: Option[Int] = None) extends StorableWithFactory[ch.model.DataLabel]
{
	override def factory = EntityLabel
	
	override def valueProperties = Vector("id" -> id, "targetType" -> targetTypeId)
}
