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
	  * @return Table that contains a list of all companies
	  */
	def company = apply("company")
	/**
	  * @return Table for company data labels / fields
	  */
	def companyDataLabel = apply("company_data_label")
	/**
	 * @return Table for company label groups
	 */
	def companyDataLabelGroup = apply("company_data_label_group")
	/**
	 * @return Table that links company labels to groups
	 */
	def companyDataLabelGroupContent = apply("company_data_label_group_content")
	/**
	  * @return Table for read company data
	  */
	def companyData = apply("company_data")
	/**
	  * @return Table for company data read events
	  */
	def companyDataRead = apply("company_data_read")
	/**
	  * @return Table that contains a list of all contacts
	  */
	def contact = apply("contact")
	/**
	  * @return Table that contains descriptive labels / fields for contact data
	  */
	def contactDataLabel = apply("contact_data_label")
	/**
	  * @return Table that contains contact data
	  */
	def contactData = apply("contact_data")
	/**
	  * @return Table that contains contact data read events
	  */
	def contactDataRead = apply("contact_data_read")
	/**
	  * @return Table that contains all possible contact roles
	  */
	def contactRole = apply("contact_role")
	/**
	  * @return Table that contains links between contacts, roles and companies
	  */
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
	  * @return Table that contains profiling results (links between companies and segments per profiling event)
	  */
	def companySegmentConnection = apply("company_segment_connection")
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
