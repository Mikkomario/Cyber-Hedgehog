package ch.granite.model

import utopia.flow.generic.ValueConversions._
import ch.model.DataLabel
import utopia.flow.datastructure.immutable.Value

/**
 * Used for linking granite multiselect / drop down options to entity labels
 * @author Mikko Hilpinen
 * @since 6.10.2019, v2+
 * @param id Unique id of this mapping
 * @param option Mapped select option
 * @param label Mapped label
 * @param valueWhenFound Value assigned to label on selection
 * @param isDeterministic Whether negated value should be assigned when not selected
 */
case class OptionLabelMapping(id: Int, option: SelectOption, override val label: DataLabel, valueWhenFound: Boolean,
							  isDeterministic: Boolean) extends LabelMapping
{
	override def toString = s"Granite option ${option.field.graniteId}:${option.graniteId} -> Label ${label.id}"
	
	/**
	 * Finds the provided value from a query result
	 * @param result A query result
	 * @return Value read for the label in this mapping
	 */
	override def apply(result: QueryResult): Option[Value] =
	{
		// In case of an option, checks whether the specified option is selected in a dropdown or a multiselect
		val fieldId = option.field.graniteId
		val optionId = option.graniteId
		
		val wasSelected =
		{
			if (result.dropDownIds.contains(fieldId))
				Some(result.dropDownIds(fieldId).contains(optionId))
			else if (result.multiSelectIds.contains(fieldId))
				Some(result.multiSelectIds(fieldId).contains(optionId))
			else
				None
		}
		
		// May not specify a value if
		// a) field was not found from results or
		// b) option wasn't selected and wasn't deterministic
		wasSelected.flatMap { selected => if (selected) Some(valueWhenFound) else if (isDeterministic) Some(!valueWhenFound) else None }
	}
}
