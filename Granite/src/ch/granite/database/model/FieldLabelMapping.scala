package ch.granite.database.model

import ch.database.model.EntityLabel
import utopia.flow.generic.ValueConversions._
import ch.granite.database.Tables
import ch.granite.model
import ch.util.Log
import utopia.vault.model.immutable.{Row, StorableWithFactory}
import utopia.vault.model.immutable.factory.FromRowFactory

import scala.util.{Failure, Success}

object FieldLabelMapping extends FromRowFactory[model.FieldLabelMapping]
{
	// IMPLEMENTED	----------------------
	
	override def apply(row: Row) =
	{
		// Must be able to parse both field and label
		Field(row).flatMap { field =>
			EntityLabel(row).flatMap { label =>
				table.requirementDeclaration.validate(row(table)).toTry match
				{
					case Success(myModel) => Some(model.FieldLabelMapping(myModel("id").getInt, field, label))
					case Failure(error) => Log(error, s"Failed to parse FieldLabelMapping from $row"); None
				}
			}
		}
	}
	
	override def table = Tables.fieldLabelMapping
	
	override def joinedTables = Field.tables ++ EntityLabel.tables
	
	
	// OTHER	------------------------
	
	/**
	 * @param fieldId Id of linked field
	 * @return A model with only field id set
	 */
	def withFieldId(fieldId: Int) = FieldLabelMapping(fieldId = Some(fieldId))
}

/**
 * Used for interacting with field label mapping DB data
 * @author Mikko Hilpinen
 * @since 6.10.2019, v2+
 */
case class FieldLabelMapping(id: Option[Int] = None, fieldId: Option[Int] = None, labelId: Option[Int] = None)
	extends StorableWithFactory[model.FieldLabelMapping]
{
	override def factory = FieldLabelMapping
	
	override def valueProperties = Vector("id" -> id, "field" -> fieldId, "label" -> labelId)
}
