package ch.database.model

import utopia.flow.generic.ValueConversions._
import ch.database.Tables
import utopia.flow.datastructure.immutable
import utopia.flow.datastructure.immutable.Constant
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.immutable.factory.StorableFactoryWithValidation

/**
  * Used for reading & parsing contact role data from DB
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
@deprecated("Replaced with EntityLinkType", "v2")
object ContactRole extends StorableFactoryWithValidation[ch.model.ContactRole]
{
	// IMPLEMENTED	-------------------
	
	override def table = Tables.contactRole
	
	override protected def fromValidatedModel(valid: immutable.Model[Constant]) = ch.model.ContactRole(valid("id").getInt,
		valid("isInsideCompany").getBoolean)
	
	
	// OTHER	------------------------
	
	/**
	  * @return Model with isInsideCompany set to true
	  */
	def insideCompany = ContactRole(isInsideCompany = Some(true))
}

@deprecated("Replaced with EntityLinkType", "v2")
case class ContactRole(id: Option[Int] = None, isInsideCompany: Option[Boolean] = None)
	extends StorableWithFactory[ch.model.ContactRole]
{
	override def factory = ContactRole
	
	override def valueProperties = Vector("id" -> id, "isInsideCompany" -> isInsideCompany)
}
