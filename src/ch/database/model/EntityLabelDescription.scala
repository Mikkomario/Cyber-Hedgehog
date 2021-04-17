package ch.database.model

import java.time.Instant

import ch.database.Tables
import utopia.flow.generic.ValueConversions._
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.factory.FromValidatedRowModelFactory

object EntityLabelDescription extends FromValidatedRowModelFactory[ch.model.EntityLabelDescription]
{
	// IMPLEMENTED	-----------------------------
	
	override protected def fromValidatedModel(model: Model[Constant]) = ch.model.EntityLabelDescription(model("id").getInt,
		model("label").getInt, model("name").getString, model("description").string, model("language").getInt,
		model("created").getInstant, model("deprecatedAfter").instant)
	
	override def table = Tables.entityLabelDescription
	
	
	// OTHER	---------------------------------
	
	/**
	 * @param labelId Id of targeted label
	 * @return A model with only label id set
	 */
	def withLabelId(labelId: Int) = EntityLabelDescription(labelId = Some(labelId))
	
	/**
	 * @param languageId Id of targeted language
	 * @return A model with only language id set
	 */
	def withLangaugeId(languageId: Int) = EntityLabelDescription(languageId = Some(languageId))
	
	/**
	 * @param deprecationTime Time when this description was deprecated
	 * @return A model with only deprecation time set
	 */
	def deprecatedAfter(deprecationTime: Instant) = EntityLabelDescription(deprecatedAfter = Some(deprecationTime))
	
	/**
	 * Creates a new description ready to be inserted to DB
	 * @param labelId Id of targeted label
	 * @param name New label name in target language
	 * @param description New label description in target language
	 * @param languageId Id of targeted language
	 * @param created Creation time for this label
	 * @return A new label description
	 */
	def forInsert(labelId: Int, name: String, description: Option[String], languageId: Int, created: Instant) =
		EntityLabelDescription(None, Some(labelId), Some(name), description, Some(languageId), Some(created))
}

/**
 * Used for interacting with entity label description data in DB
 * @author Mikko Hilpinen
 * @since 25.10.2019, v3+
 */
case class EntityLabelDescription(id: Option[Int] = None, labelId: Option[Int] = None, name: Option[String] = None,
								  description: Option[String] = None, languageId: Option[Int] = None,
								  created: Option[Instant] = None, deprecatedAfter: Option[Instant] = None)
	extends StorableWithFactory[ch.model.EntityLabelDescription]
{
	// IMPLEMENTED	----------------------------
	
	override def factory = EntityLabelDescription
	
	override def valueProperties = Vector("id" -> id, "label" -> labelId, "name" -> name,
		"description" -> description, "language" -> languageId, "created" -> created,
		"deprecatedAfter" -> deprecatedAfter)
	
	
	// OTHER	--------------------------------
	
	/**
	 * @param languageId Id of language for this label
	 * @return A copy of this model with language code set
	 */
	def withLanguageId(languageId: Int) = copy(languageId = Some(languageId))
}
