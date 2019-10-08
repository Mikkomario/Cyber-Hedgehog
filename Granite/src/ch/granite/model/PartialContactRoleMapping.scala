package ch.granite.model

/**
  * Represents a mapping between a select option and contact role. Doesn't contain select option data, however
  * @author Mikko Hilpinen
  * @since 14.7.2019, v0.1+
  * @param id This mapping's unique id
  * @param optionId Id of associated select option
  * @param roleId id of associated contact role
  */
@deprecated("Replaced with LinkTypeMapping", "v2")
case class PartialContactRoleMapping(id: Int, optionId: Int, roleId: Int)
{
	/**
	  * Completes this partial mapping by adding option field data
	  * @param options The option fields that are available
	  * @return A complete contact role mapping. None if options didn't contain a suitable model
	  */
	def complete(options: Traversable[SelectOption]) = options.find { _.id == optionId }.map {
		ContactRoleMapping(id, _, roleId) }
}
