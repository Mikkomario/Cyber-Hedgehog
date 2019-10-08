package ch.database

import utopia.vault.database

/**
  * Keeps track of all used database tables
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
object Tables
{
	// ATTRIBUTES	----------------------
	
	private val databaseName = "cyber_hedgehog"
	
	
	// COMPUTED	------------------------
	
	/**
	 * @return Table that contains all entity types
	 */
	def entityType = apply("entity_type")
	/**
	 * @return Table that contains all companies, contacts, etc.
	 */
	def entity = apply("entity")
	/**
	  * @return Table that contains a list of all companies
	 *  @deprecated Company & contact tables replaced with common entity tables
	  */
	@deprecated("Replaced with entity", "v2")
	def company = apply("company")
	/**
	 * @return Table that contains descriptive labels for all entities
	 */
	def entityLabel = apply("entity_label")
	/**
	 * @return Table that contains specifications for labels
	 */
	def entityLabelConfiguration = apply("entity_label_configuration")
	/**
	  * @return Table for company data labels / fields
	 *  @deprecated Replaced with entityLabel
	  */
	@deprecated("Replaced with entityLabel", "v2")
	def companyDataLabel = apply("company_data_label")
	/**
	 * @return Table for company label groups
	 */
	def entityLabelGroup = apply("entity_label_group")
	/**
	 * @return Table that links company labels to groups
	 */
	def entityLabelGroupContent = apply("entity_label_group_content")
	/**
	 * @return Table for all collected entity data
	 */
	def entityData = apply("entity_data")
	/**
	  * @return Table for read company data
	  */
	@deprecated("Replaced with entityData", "v2")
	def companyData = apply("company_data")
	/**
	 * @return Table that contains all data read events
	 */
	def dataRead = apply("data_read")
	/**
	  * @return Table for company data read events
	  */
	@deprecated("Replaced with dataRead", "v2")
	def companyDataRead = apply("company_data_read")
	/**
	  * @return Table that contains a list of all contacts
	  */
	@deprecated("replaced with entity")
	def contact = apply("contact")
	/**
	  * @return Table that contains descriptive labels / fields for contact data
	  */
	@deprecated("Replaced with entityLabel", "v2")
	def contactDataLabel = apply("contact_data_label")
	/**
	  * @return Table that contains contact data
	  */
	@deprecated("Replaced with entityData", "v2")
	def contactData = apply("contact_data")
	/**
	  * @return Table that contains contact data read events
	  */
	@deprecated("Replaced with dataRead", "v2")
	def contactDataRead = apply("contact_data_read")
	/**
	 * @return Table that contains different entity relation types
	 */
	def entityLinkType = apply("entity_link_type")
	/**
	 * @return Table that contains links between entities
	 */
	def entityLink = apply("entity_link")
	/**
	  * @return Table that contains all possible contact roles
	  */
	@deprecated("Replaced with entityLinkType", "v2")
	def contactRole = apply("contact_role")
	/**
	  * @return Table that contains links between contacts, roles and companies
	  */
	@deprecated("Replaced with entityLink", "v2")
	def contactCompanyRole = apply("contact_company_role")
	/**
	  * @return Table for company profiling segments
	  */
	def segment = apply("segment")
	/**
	  * @return Table for company profiling history
	  */
	def segmentProfiling = apply("segment_profiling")
	/**
	  * @return Table that contains profiling results (links between entities and segments per profiling event)
	  */
	def segmentContent = apply("segment_content")
	/**
	  * @return Table that contains filter used to determine segment contents
	  */
	def segmentFilter = apply("segment_filter")
	/**
	  * @return Table that contains hierarchical conditions used in segment filtering
	  */
	def segmentFilterConditionCombo = apply("segment_filter_condition_combination")
	/**
	  * @return Table that contains individual conditions used in segment filtering
	  */
	def segmentFilterCondition = apply("segment_filter_condition_part")
	/**
	 * @return Table that contains versions of algorithm used for cyber risk score calculation
	 */
	def riskAlgorithm = apply("cyber_risk_algorithm")
	/**
	 * @return Table used for cyber risk score modifiers (parameters)
	 */
	def riskScoreModifier = apply("cyber_risk_score_modifier")
	/**
	 * @return Table for calculated cyber risk scores
	 */
	def riskScore = apply("cyber_risk_score")
	/**
	 * @return Table that contains cyber risk scoring timestamps
	 */
	def riskScoringEvent = apply("cyber_risk_scoring_event")
	
	
	// OTHER	------------------------
	
	private def apply(tableName: String) = database.Tables(databaseName, tableName)
}
