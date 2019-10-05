package ch.granite.model

import utopia.flow.generic.ValueConversions._
import ch.model.DataLabel
import utopia.flow.datastructure.immutable.Value

/**
  * Represents a link between a granite field and a data label of some sort
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  * @param id This mapping's unique identifier
  * @param field The granite field being mapped (either a field or a select option)
  * @param label The company data label to which the field is mapped to
  */
case class LabelMapping(id: Int, field: Either[SelectOption, Field], label: DataLabel)
{
	/**
	  * Finds the provided value from a query result
	  * @param result A query result
	  * @return Value read for the label in this mapping
	  */
	def apply(result: QueryResult): Option[Value] = field match
	{
		// In case of an option, checks whether the specified option is selected in a dropdown or a multiselect
		case Left(option) =>
			val fieldId = option.field.graniteFieldId
			val optionId = option.optionId
			
			if (result.dropDownIds.contains(fieldId))
				Some(result.dropDownIds(fieldId).contains(optionId))
			else if (result.multiSelectIds.contains(fieldId))
				Some(result.multiSelectIds(fieldId).contains(optionId))
			else
				None
		
		// In case of a raw field, simply finds the basic value
		case Right(rawField) => result.baseValues.get(rawField.graniteFieldId)
	}
}
