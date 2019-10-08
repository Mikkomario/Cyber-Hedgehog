package ch.granite.database.model

import ch.granite.model
import ch.granite.database.Tables
import utopia.flow.datastructure.immutable.Model
import utopia.flow.datastructure.immutable.Constant
import utopia.vault.model.immutable.factory.LinkedStorableFactory

/**
  * Used for reading & parsing granite selection option data from DB
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
object SelectOption extends LinkedStorableFactory[model.SelectOption, model.Field]
{
	// IMPLEMENTED	------------------
	
	override def table = Tables.selectOption
	
	override def childFactory = Field
	
	override def apply(model: Model[Constant], child: model.Field) = table.requirementDeclaration.validate(
		model).toTry.map { valid => ch.granite.model.SelectOption(valid("id").getInt, child, valid("graniteId").getInt) }
}
