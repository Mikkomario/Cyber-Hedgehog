package ch.granite.database.model

import ch.granite.model.PartialLabelMapping
import ch.model.DataLabel
import utopia.vault.model.immutable.Row
import utopia.vault.model.immutable.factory.FromRowFactory

/**
  * Used for reading & parsing label mapping data from DB
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
trait LabelMappingFactory[+M] extends FromRowFactory[PartialLabelMapping]
{
	// ABSTRACT	-----------------
	
	/**
	  * @return Factory used for constructing data labels for these mappings
	  */
	protected def labelFactory: FromRowFactory[DataLabel]
	
	/**
	  * @param fieldId Field's id
	  * @return A model with field id set
	  */
	def withFieldId(fieldId: Int): M
	
	/**
	  * @param optionId SelectOption's id
	  * @return A model with option id set
	  */
	def withOptionId(optionId: Int): M
	
	
	// COMPUTED	-----------------
	
	/**
	  * @return Column in table that contains link to field
	  */
	def fieldColumn = table("field")
	
	/**
	  * @return Column in table that contains link to select option
	  */
	def optionColumn = table("optionField")
	
	
	// IMPLEMENTED	-------------
	
	override def apply(row: Row) =
	{
		// Parses label, followed by own data
		labelFactory(row).flatMap { label =>
			
			val model = row(table)
			val id = model("id").int
			val fieldId = model("field").int
			val optionId = model("optionField").int
			
			// Either field id or option id must be defined
			if (id.isDefined && (fieldId.isDefined || optionId.isDefined))
				Some(PartialLabelMapping(id.get, fieldId, optionId, label))
			else
				None
		}
	}
	
	override def joinedTables = labelFactory.tables
}
