package ch.granite.database.model

import utopia.flow.generic.ValueConversions._
import ch.granite
import ch.granite.database.Tables
import utopia.flow.datastructure.immutable
import utopia.flow.datastructure.immutable.Constant
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.immutable.factory.StorableFactoryWithValidation

/**
  * Used for reading & parsing field data from DB
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
object Field extends StorableFactoryWithValidation[granite.model.Field]
{
	// IMPLEMENTED	---------------------
	
	override def table = Tables.field
	
	override protected def fromValidatedModel(valid: immutable.Model[Constant]) = granite.model.Field(valid("id").getInt,
		valid("service").getInt, valid("graniteId").getInt)
	
	
	// OTHER	-------------------------
	
	/**
	  * @param id Granite service id
	  * @return A model with service id set
	  */
	def withServiceId(id: Int) = Field(serviceId = Some(id))
}

/**
  * Used for searching & updating granite fields
  * @param id Unique id
  * @param serviceId Id of service this field is part of
  * @param graniteId Granite field id
  * @author Mikko Hilpinen
  * @since 13.7.2019, v0.1+
  */
case class Field(id: Option[Int] = None, serviceId: Option[Int], graniteId: Option[Int] = None) extends
	StorableWithFactory[granite.model.Field]
{
	override def factory = Field
	
	override def valueProperties = Vector("id" -> id, "service" -> serviceId, "graniteId" -> graniteId)
}
