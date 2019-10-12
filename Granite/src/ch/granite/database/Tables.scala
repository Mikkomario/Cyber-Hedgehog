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
	
	
	// OTHER	-------------------------
	
	private def apply(tableName: String) = database.Tables(databaseName, tableName)
}
