package ch.database.model

import utopia.flow.util.CollectionExtensions._
import ch.util.Log
import utopia.flow.datastructure.immutable.Value
import utopia.flow.parse.JSONReader
import utopia.vault.model.immutable.Row
import utopia.vault.model.immutable.factory.FromRowFactory

/**
  * Used for reading data models from DB
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
trait DataFactory[+M] extends FromRowFactory[ch.model.Data]
{
	// ABSTRACT	-------------------
	
	/**
	  * @return factory used for producing dataRead models
	  */
	def readFactory: DataReadFactory[_]
	
	/**
	  * @return Factory used for producing label models
	  */
	def labelFactory: DataLabelFactory[_]
	
	def apply(id: Option[Int] = None, readId: Option[Int] = None, labelId: Option[Int] = None, value: Option[Value] = None): M
	
	
	// IMPLEMENTED	---------------
	
	override def apply(row: Row) =
	{
		// Read and label information must be successfully parsed
		readFactory(row).flatMap { read =>
			
			labelFactory(row).flatMap { label =>
					
				// Reads own data
				val result = table.requirementDeclaration.validate(row(table)).toTry.map { valid =>
					
					val value = valid("value").string.flatMap(JSONReader(_).toOption.flatMap {
						_.castTo(label.dataType.flowType) }) getOrElse Value.empty
					
					ch.model.Data(valid("id").getInt, read, label, value)
				}
				
				result.failure.foreach { Log(_, s"Failed to parse CompanyData from $row") }
				result.toOption
			}
		}
	}
	
	override def joinedTables = readFactory.tables ++ labelFactory.tables
	
	
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
}
