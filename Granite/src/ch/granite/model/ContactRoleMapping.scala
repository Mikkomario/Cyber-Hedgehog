package ch.granite.model

/**
  * Maps an option field value to a contact role
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  * @param id This mapping's unique identifier
  * @param option The option value being mapped
  * @param roleId Id of the role being mapped
  */
case class ContactRoleMapping(id: Int, option: SelectOption, roleId: Int)
{
	/**
	  * Checks whether this role maps to any result values
	  * @param result A Granite read result
	  * @return Whether this role can be found from result. None if the targeted field was missing.
	  */
	def apply(result: QueryResult) =
	{
		val fieldId = option.field.graniteFieldId
		val optionId = option.optionId
		
		if (result.dropDownIds.contains(fieldId))
			Some(result.dropDownIds(fieldId).contains(optionId))
		else if (result.multiSelectIds.contains(fieldId))
			Some(result.multiSelectIds(fieldId).contains(optionId))
		else
			None
	}
}
