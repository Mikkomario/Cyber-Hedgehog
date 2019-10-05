package ch.mailchimp.database

import utopia.vault.database

/**
  * Used for retrieving tables related to mail chimp interface
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object Tables
{
	// ATTRIBUTES	----------------------
	
	private val databaseName = "cyber_hedgehog"
	
	
	// COMPUTED	------------------------
	
	/**
	  * @return Table that contains mail chimp lists
	  */
	def contactList = apply("mail_chimp_list")
	/**
	  * @return Table that contains merge fields for each list
	  */
	def mergeField = apply("mail_chimp_merge_field")
	/**
	  * @return Table that contains segments under each list
	  */
	def contactSegment = apply("mail_chimp_segment")
	/**
	  * @return Table that contains contact update events
	  */
	def contactUpdateEvent = apply("mail_chimp_contact_update_event")
	/**
	  * @return Table that contains segment update events
	  */
	def segmentUpdateEvent = apply("mail_chimp_segment_update_event")
	
	
	// OTHER	-------------------------
	
	private def apply(tableName: String) = database.Tables(databaseName, tableName)
}
