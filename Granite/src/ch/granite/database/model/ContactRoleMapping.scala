package ch.granite.database.model

import ch.granite.database.Tables
import ch.granite.model.PartialContactRoleMapping
import utopia.flow.datastructure.immutable
import utopia.flow.datastructure.immutable.Constant
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.immutable.factory.StorableFactoryWithValidation

object ContactRoleMapping extends StorableFactoryWithValidation[PartialContactRoleMapping]
{
	// IMPLEMENTED	---------------------
	
	override protected def fromValidatedModel(valid: immutable.Model[Constant]) = PartialContactRoleMapping(
		valid("id").getInt, valid("optionField").getInt, valid("role").getInt)
	
	override def table = Tables.contactRoleMapping
}

/**
  * Used for searching & updating contact role mappings in DB
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
case class ContactRoleMapping(id: Option[Int] = None, optionFieldId: Option[Int] = None, roleId: Option[Int] = None)
	extends StorableWithFactory[PartialContactRoleMapping]
{
	override def factory = ContactRoleMapping
	
	override def valueProperties = Vector("id" -> id, "optionField" -> optionFieldId, "role" -> roleId)
}
