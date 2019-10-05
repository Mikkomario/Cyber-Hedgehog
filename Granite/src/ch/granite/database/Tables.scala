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
	  * @return Contains field to company data label mappings
	  */
	def companyLabelMapping = apply("granite_company_label_mapping")
	/**
	  * @return Contains field to contact data label mappings
	  */
	def contactLabelMapping = apply("granite_contact_label_mapping")
	/**
	  * @return Contains mappings between granite options and contact roles
	  */
	def contactRoleMapping = apply("granite_contact_role_mapping")
	/**
	  * @return Contains all registered granite fields
	  */
	def field = apply("granite_field")
	/**
	  * @return Contains all registered granite dropdown and multiselect options
	  */
	def selectOption = apply("granite_multiselect_option")
	
	
	// OTHER	-------------------------
	
	private def apply(tableName: String) = database.Tables(databaseName, tableName)
}
