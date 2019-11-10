package ch.database.model

import java.time.Instant

import ch.database.Tables
import ch.model.DataType
import utopia.flow.generic.ValueConversions._
import utopia.flow.datastructure.template
import utopia.flow.datastructure.template.Property
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.immutable.factory.StorableFactory

import scala.util.{Failure, Success}

object EntityLabelConfiguration extends StorableFactory[ch.model.EntityLabelConfiguration]
{
	// IMPLEMENTED	----------------------
	
	override def table = Tables.entityLabelConfiguration
	
	override def apply(model: template.Model[Property]) = table.requirementDeclaration.validate(
		model).toTry.flatMap { valid =>
		val dataType = DataType.forInt(valid("dataType").getInt)
		if (dataType.isDefined)
			Success(ch.model.EntityLabelConfiguration(valid("id").getInt, valid("label").getInt, dataType.get,
				valid("isIdentifier").getBoolean, valid("isEmail").getBoolean, valid("created").getInstant,
				valid("deprecatedAfter").instant))
		else
			Failure(new NoSuchElementException(s"Couldn't recognize data type: ${valid("dataType").getInt}"))
	}
	
	
	// OTHER	--------------------------
	
	/**
	 * A model where isIdentifier is set to true
	 */
	def identifier = apply(isIdentifier = Some(true))
	
	/**
	 * A model where isIdentifier is set to false
	 */
	def nonIdentifier = apply(isIdentifier = Some(false))
	
	/**
	 * @return A model where isEmail is set to true
	 */
	def email = apply(isEmail = Some(true))
	
	/**
	 * @param labelId Id of targeted label
	 * @return A new model with only label id set
	 */
	def withLabelId(labelId: Int) = EntityLabelConfiguration(labelId = Some(labelId))
	
	/**
	 * @param deprecationTime Time when label became deprecated
	 * @return A model with only deprecation time set
	 */
	def deprecatedAfter(deprecationTime: Instant) = EntityLabelConfiguration(deprecatedAfter = Some(deprecationTime))
	
	/**
	 * Creates a new model ready to be inserted
	 * @param labelId Id of targeted label
	 * @param dataType New data type of label
	 * @param isIdentifier Whether label should be identifier
	 * @param isEmail Whether label should be email
	 * @param created Creation time of label
	 * @return A new model ready to be inserted
	 */
	def forInsert(labelId: Int, dataType: DataType, isIdentifier: Boolean, isEmail: Boolean, created: Instant = Instant.now()) =
		EntityLabelConfiguration(None, Some(labelId), Some(dataType), Some(isIdentifier), Some(isEmail), Some(created))
}

/**
 * Used for interacting with entity label configurations in DB
 * @author Mikko Hilpinen
 * @since 5.10.2019, v2+
 */
case class EntityLabelConfiguration(id: Option[Int] = None, labelId: Option[Int] = None,
									dataType: Option[DataType] = None, isIdentifier: Option[Boolean] = None,
									isEmail: Option[Boolean] = None, created: Option[Instant] = None,
									deprecatedAfter: Option[Instant] = None)
	extends StorableWithFactory[ch.model.EntityLabelConfiguration]
{
	override def factory = EntityLabelConfiguration
	
	override def valueProperties = Vector("id" -> id, "label" -> labelId,
		"dataType" -> dataType.map { _.toInt }, "isIdentifier" -> isIdentifier, "email" -> isEmail,
		"created" -> created, "deprecatedAfter" -> deprecatedAfter)
}
