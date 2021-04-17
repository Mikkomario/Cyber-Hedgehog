package ch.database.model

import java.time.Instant
import ch.database.Tables
import utopia.flow.datastructure.immutable.Value
import utopia.vault.model.immutable.{Row, StorableWithFactory}
import utopia.flow.generic.ValueConversions._
import utopia.flow.parse.JSONReader
import utopia.vault.nosql.factory.FromRowFactory
import utopia.vault.sql.JoinType

object EntityData extends FromRowFactory[ch.model.Data]
{
	// IMPLEMENTED	--------------------
	
	override def joinType = JoinType.Left
	
	override def apply(row: Row) =
	{
		// Read and label information must be successfully parsed
		DataRead(row).flatMap { read =>
			EntityLabel(row).flatMap { label =>
				// Reads own data
				table.requirementDeclaration.validate(row(table)).toTry.map { valid =>
					
					val value = valid("value").string.flatMap(JSONReader(_).toOption.flatMap {
						_.castTo(label.dataType.flowType) }) getOrElse Value.empty
					
					ch.model.Data(valid("id").getInt, read, label, value, valid("deprecatedAfter").instant)
				}
			}
		}
	}
	
	override def table = Tables.entityData
	
	override def joinedTables = DataRead.tables ++ EntityLabel.tables
	
	
	// OTHER	-------------------
	
	/**
	 * Creates a new data model ready to be inserted to DB
	 * @param readId Associated data read's id
	 * @param labelId Associated label's id
	 * @param value New label value
	 * @return A model ready to be inserted
	 */
	def forInsert(readId: Int, labelId: Int, value: Value) = apply(None, Some(readId), Some(labelId), Some(value))
	
	/**
	 * @param labelId Label unique id
	 * @return A data model with label id set
	 */
	def withLabelId(labelId: Int) = apply(labelId = Some(labelId))
	
	/**
	 * @param readId Id of data read event
	 * @return Model with only read id set
	 */
	def withReadId(readId: Int) = apply(readId = Some(readId))
	
	/**
	 * @param deprecationTime Time of deprecation
	 * @return A model with only deprecation time set
	 */
	def withDeprecatedAfter(deprecationTime: Instant) = apply(deprecatedAfter = Some(deprecationTime))
}

/**
 * Used for interacting with entity data in DB
 * @author Mikko Hilpinen
 * @since 5.10.2019, v2+
 */
case class EntityData(id: Option[Long] = None, readId: Option[Int] = None, labelId: Option[Int] = None,
					  value: Option[Value] = None, deprecatedAfter: Option[Instant] = None)
	extends StorableWithFactory[ch.model.Data]
{
	// IMPLEMENTED	---------------------
	
	override def factory = EntityData
	
	override def valueProperties = Vector("id" -> id, "read" -> readId, "label" -> labelId,
		"value" -> value.map { _.toJson }, "deprecatedAfter" -> deprecatedAfter)
	
	
	// OTHER	------------------------
	
	/**
	 * @param value A value
	 * @return A copy of this model with specified value
	 */
	def withValue(value: Value) = copy(value = Some(value))
}
