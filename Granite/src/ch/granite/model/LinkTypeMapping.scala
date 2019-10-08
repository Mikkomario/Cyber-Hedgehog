package ch.granite.model

/**
 * Used for linking granite multiselect / drop down options to entity link types
 * @author Mikko Hilpinen
 * @since 6.10.2019, v2+
 * @param id Unique id of this mapping
 * @param option Mapped select option
 * @param originTypeId Id of entity type of link origin
 * @param targetTypeId Id of entity type of link target
 * @param linkTypeId If of the type of generated link
 */
case class LinkTypeMapping(id: Int, option: SelectOption, originTypeId: Int, targetTypeId: Int, linkTypeId: Int)
{
	/**
	 * Checks whether this link maps to any result values
	 * @param result A Granite read result
	 * @return Whether this link can be found from result. None if the targeted field was missing.
	 */
	def apply(result: QueryResult) =
	{
		val fieldId = option.field.graniteId
		val optionId = option.graniteId
		
		if (result.dropDownIds.contains(fieldId))
			Some(result.dropDownIds(fieldId).contains(optionId))
		else if (result.multiSelectIds.contains(fieldId))
			Some(result.multiSelectIds(fieldId).contains(optionId))
		else
			None
	}
}
