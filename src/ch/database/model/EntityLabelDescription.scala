package ch.database.model

import java.time.Instant

import ch.database.Tables
import utopia.flow.generic.ValueConversions._
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.immutable.factory.StorableFactoryWithValidation

object EntityLabelDescription extends StorableFactoryWithValidation[ch.model.EntityLabelDescription]
{
	// IMPLEMENTED	-----------------------------
	
	override protected def fromValidatedModel(model: Model[Constant]) = ch.model.EntityLabelDescription(model("id").getInt,
		model("label").getInt, model("name").getString, model("description").string, model("languageCode").getString,
		model("created").getInstant, model("deprecatedAfter").instant)
	
	override def table = Tables.entityLabelDescription
	
	
	// OTHER	---------------------------------
	
	/**
	 * @param labelId Id of targeted label
	 * @return A model with only label id set
	 */
	def withLabelId(labelId: Int) = EntityLabelDescription(labelId = Some(labelId))
}

/**
 * Used for interacting with entity label description data in DB
 * @author Mikko Hilpinen
 * @since 25.10.2019, v3+
 */
case class EntityLabelDescription(id: Option[Int] = None, labelId: Option[Int] = None, name: Option[String] = None,
								  description: Option[String] = None, languageCode: Option[String] = None,
								  created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None)
	extends StorableWithFactory[ch.model.EntityLabelDescription]
{
	// IMPLEMENTED	----------------------------
	
	override def factory = EntityLabelDescription
	
	override def valueProperties = Vector("id" -> id, "label" -> labelId, "name" -> name,
		"description" -> description, "languageCode" -> languageCode, "created" -> created,
		"deprecatedAfter" -> deprecatedAfter)
	
	
	// OTHER	--------------------------------
	
	/**
	 * @param code ISO language code
	 * @return A copy of this model with language code set
	 */
	def withLanguageCode(code: String) = copy(languageCode = Some(code))
}
