package ch.granite.model

import ch.model.DataLabel
import ch.util.Log

/**
  * This label contains all information except field data, which can be added separately
  * @author Mikko Hilpinen
  * @since 14.7.2019, v0.1+
  * @param id This mapping's unique id
  * @param fieldId The id of the field associated with this mapping (None if option associated instead)
  * @param optionId The id of the select option associated with this mapping (None if field associated instead)
  * @param label The data label that is associated with this mapping
  */
@deprecated("Replaced with FieldLabelMapping and OptionLabelMapping", "v2")
case class PartialLabelMapping(id: Int, fieldId: Option[Int], optionId: Option[Int], label: DataLabel)
{
	/**
	  * Completes this mapping by adding field information
	  * @param field Model for associated field (should have same field id)
	  * @return A complete label mapping
	  */
	def withField(field: Field) =
	{
		if (!fieldId.contains(field.id))
			Log.warning(s"Field $field doesn't match mapping's ($id) field id $fieldId")
		LabelMappingOld(id, Right(field), label)
	}
	
	/**
	  * Completes this mapping by adding option information
	  * @param option Model for associated option (should have same option id)
	  * @return A complete label mapping
	  */
	def withOption(option: SelectOption) =
	{
		if (!optionId.contains(option.id))
			Log.warning(s"SelectOption $option doesn't match mapping's ($id) option id $optionId")
		LabelMappingOld(id, Left(option), label)
	}
	
	/**
	  * Completes this label mapping using field or option data
	  * @param fields The fields that are available
	  * @param options The options that are available
	  * @return A complete label mapping. None if none of the provided fields / options matched those of this mapping
	  */
	def complete(fields: Traversable[Field], options: Traversable[SelectOption]) =
	{
		fieldId.flatMap { fid => fields.find { _.id == fid } }.map(withField).orElse(optionId.flatMap {
			oid => options.find { _.id == oid } }.map(withOption))
	}
}
