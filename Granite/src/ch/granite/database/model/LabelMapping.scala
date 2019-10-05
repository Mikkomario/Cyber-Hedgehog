package ch.granite.database.model

import ch.granite.model.PartialLabelMapping
import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory

/**
  * Used for searching & updating label mapping data
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
trait LabelMapping extends StorableWithFactory[PartialLabelMapping]
{
	/**
	  * @return This mapping's identifier
	  */
	def id: Option[Int]
	/**
	  * @return Id of mapped field
	  */
	def fieldId: Option[Int]
	/**
	  * @return Id of mapped option
	  */
	def optionId: Option[Int]
	/**
	  * @return Id of mapped label
	  */
	def labelId: Option[Int]
	
	override def valueProperties = Vector("id" -> id, "field" -> fieldId, "optionField" -> optionId, "label" -> labelId)
}
