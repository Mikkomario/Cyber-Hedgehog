package ch.granite.database.model

import ch.granite.database.Tables
import utopia.flow.datastructure.template.{Model, Property}
import utopia.vault.model.immutable.Row
import utopia.vault.model.immutable.factory.FromRowFactory

/**
  * Used for reading & parsing granite selection option data from DB
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
object SelectOption extends FromRowFactory[ch.granite.model.SelectOption]
{
	// IMPLEMENTED	------------------
	
	override def table = Tables.selectOption
	
	override def joinedTables = Vector(Field.table)
	
	override def apply(row: Row): Option[ch.granite.model.SelectOption] = Field(row).flatMap { apply(row(table), _) }
	
	
	// OPERATORS	------------------
	
	/**
	  * Parses an option in case where the field portion has already been parsed
	  * @param optionModel A model that represents a select option
	  * @param field Field associated with this option
	  * @return A parsed select option
	  */
	def apply(optionModel: Model[Property], field: ch.granite.model.Field): Option[ch.granite.model.SelectOption] =
	{
		val id = optionModel("id").int
		val optionId = optionModel("optionId").int
		
		if (id.isDefined && optionId.isDefined)
			Some(ch.granite.model.SelectOption(id.get, field, optionId.get))
		else
			None
	}
}
