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
	 * @return Table that contains descriptive labels for all entities
	 */
	def entityLabel = apply("entity_label")
	/**
	 * @return Table that contains descriptions for labels in various different languages
	 */
	def entityLabelDescription = apply("entity_label_description")
	/**
	 * @return Table that contains specifications for labels
	 */
	def entityLabelConfiguration = apply("entity_label_configuration")
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
	 * @return Table that contains all data read events
	 */
	def dataRead = apply("data_read")
	/**
	 * @return Table that contains different entity relation types
	 */
	def entityLinkType = apply("entity_link_type")
	/**
	 * @return Table that contains links between entities
	 */
	def entityLink = apply("entity_link")
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
