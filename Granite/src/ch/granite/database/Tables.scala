package ch.granite.database

import utopia.vault.database

/**
  * Contains all tables used by granite interface (only)
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
object Tables
{
	// ATTRIBUTES	----------------------
	
	private val databaseName = "cyber_hedgehog"
	
	
	// COMPUTED	------------------------
	
	/**
	 * @return Table for all granite services used
	 */
	def service = apply("granite_service")
	/**
	 * @return Contains all registered granite fields
	 */
	def field = apply("granite_field")
	/**
	 * @return Contains all registered granite dropdown and multiselect options
	 */
	def selectOption = apply("granite_multiselect_option")
	/**
	 * @return Table that connects granite fields to entity labels
	 */
	def fieldLabelMapping = apply("granite_field_label_mapping")
	/**
	 * @return Table that connects granite multiselect options to entity labels
	 */
	def optionLabelMapping = apply("granite_option_label_mapping")
	/**
	 * @return Table that connects granite multiselect options to entity link types
	 */
	def linkTypeMapping = apply("granite_link_type_mapping")
	/**
	  * @return Contains field to company data label mappings
	  */
	@deprecated("Replaced with fieldLabelMapping and optionLabelMapping", "v2")
	def companyLabelMapping = apply("granite_company_label_mapping")
	/**
	  * @return Contains field to contact data label mappings
	  */
	@deprecated("Replaced with fieldLabelMapping and optionLabelMapping", "v2")
	def contactLabelMapping = apply("granite_contact_label_mapping")
	/**
	  * @return Contains mappings between granite options and contact roles
	  */
	@deprecated("Replaced with linkTypeMapping", "v2")
	def contactRoleMapping = apply("granite_contact_role_mapping")
	
	
	// OTHER	-------------------------
	
	private def apply(tableName: String) = database.Tables(databaseName, tableName)
}
