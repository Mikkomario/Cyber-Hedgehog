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

object EntityLabelConfiguration extends StorableFactory[ch.model.DataLabelConfiguration]
{
	// IMPLEMENTED	----------------------
	
	override def table = Tables.entityLabelConfiguration
	
	override def apply(model: template.Model[Property]) = table.requirementDeclaration.validate(
		model).toTry.flatMap { valid =>
		val dataType = DataType.forInt(valid("dataType").getInt)
		if (dataType.isDefined)
			Success(ch.model.DataLabelConfiguration(valid("id").getInt, valid("label").getInt, dataType.get,
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
	extends StorableWithFactory[ch.model.DataLabelConfiguration]
{
	override def factory = EntityLabelConfiguration
	
	override def valueProperties = Vector("id" -> id, "label" -> labelId,
		"dataType" -> dataType.map { _.toInt }, "isIdentifier" -> isIdentifier, "email" -> isEmail,
		"created" -> created, "deprecatedAfter" -> deprecatedAfter)
}
