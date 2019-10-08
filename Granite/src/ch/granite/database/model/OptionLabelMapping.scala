package ch.granite.database.model

import ch.database.model.EntityLabel
import ch.granite.database.Tables
import utopia.flow.generic.ValueConversions._
import ch.granite.model
import ch.util.Log
import utopia.vault.model.immutable.{Row, StorableWithFactory}
import utopia.vault.model.immutable.factory.FromRowFactory

import scala.util.{Failure, Success}

object OptionLabelMapping extends FromRowFactory[model.OptionLabelMapping]
{
	// IMPLEMENTED	--------------------
	
	override def apply(row: Row) =
	{
		// Both select option and label must be parseable
		SelectOption(row).flatMap { option =>
			EntityLabel(row).flatMap { label =>
				table.requirementDeclaration.validate(row(table)).toTry match
				{
					case Success(myModel) => Some(model.OptionLabelMapping(myModel("id").getInt, option, label,
						myModel("value").getBoolean, myModel("isDeterministic").getBoolean))
					case Failure(error) => Log(error, s"Failed to parse OptionLabelMapping from $row"); None
				}
			}
		}
	}
	
	override def table = Tables.optionLabelMapping
	
	override def joinedTables = SelectOption.tables ++ EntityLabel.tables
	
	
	// OTHER	-------------------------
	
	/**
	 * @param optionId Id of linked select option
	 * @return A model with only option id set
	 */
	def withOptionId(optionId: Int) = OptionLabelMapping(optionId = Some(optionId))
}

/**
 * Used for interacting with granite option label mapping DB data
 * @author Mikko Hilpinen
 * @since 6.10.2019, v2+
 */
case class OptionLabelMapping(id: Option[Int] = None, optionId: Option[Int] = None, labelId: Option[Int] = None,
							  value: Option[Boolean] = None, isDeterministic: Option[Boolean] = None)
	extends StorableWithFactory[model.OptionLabelMapping]
{
	override def factory = OptionLabelMapping
	
	override def valueProperties = Vector("id" -> id, "option" -> optionId, "label" -> labelId,
		"value" -> value, "isDeterministic" -> isDeterministic)
}
