package ch.granite.database.model

import ch.granite.database.Tables
import utopia.flow.generic.ValueConversions._
import ch.granite.model
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.immutable.factory.StorableFactoryWithValidation

object Service extends StorableFactoryWithValidation[model.Service]
{
	override protected def fromValidatedModel(model: Model[Constant]) = ch.granite.model.Service(model("id").getInt,
		model("graniteId").getInt)
	
	override def table = Tables.service
}

/**
 * Used for interacting with service data
 * @author Mikko Hilpinen
 * @since 6.10.2019, v2+
 */
case class Service(id: Option[Int] = None, graniteId: Option[Int] = None) extends StorableWithFactory[model.Service]
{
	override def factory = Service
	
	override def valueProperties = Vector("id" -> id, "graniteId" -> id)
}
