package ch.database.model

import ch.database.Tables
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.factory.StorableFactoryWithValidation

object EntityLinkType extends StorableFactoryWithValidation[ch.model.EntityLinkType]
{
	// IMPLEMENTED	--------------------
	
	override def table = Tables.entityLinkType
	
	override protected def fromValidatedModel(model: Model[Constant]) = ch.model.EntityLinkType(model("id").getInt,
		model("isContainment").getBoolean)
	
	
	// OTHER	------------------------
	
	/**
	 * @return A model with containment set to true
	 */
	def containment = EntityLinkType(isContainment = Some(true))
}

/**
 * Used for interacting with entity link type DB data
 * @author Mikko Hilpinen
 * @since 5.10.2019, v2+
 */
case class EntityLinkType(id: Option[Int] = None, isContainment: Option[Boolean] = None)
	extends StorableWithFactory[ch.model.EntityLinkType]
{
	override def factory = EntityLinkType
	
	override def valueProperties = Vector("id" -> id, "isContainment" -> isContainment)
}
