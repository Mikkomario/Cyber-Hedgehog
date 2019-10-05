package ch.granite.controller

import utopia.flow.util.CollectionExtensions._
import ch.database.{Company, Contact}
import ch.granite.model.{ContactRoleMapping, Granite, LabelMapping, QueryResult}
import ch.util.Log
import utopia.vault.database.Connection

/**
  * Handles incoming query results by saving data to DB
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
object ResultHandler
{
	/**
	  * Handles the result read from Granite API, saving new data to DB
	  * @param result The read result
	  * @return Operation result (may contain failure)
	  */
	def apply(result: QueryResult, companyLabelMappings: Traversable[LabelMapping],
			  contactLabelMappings: Traversable[LabelMapping], contactRoleMappings: Traversable[ContactRoleMapping])
			 (implicit connection: Connection) =
	{
		val sourceId = Granite.id
		// Handles identifier & non-identifier mappings separately
		val (companyNonIdentifierMappings, companyIdentifierMappings) = companyLabelMappings.divideBy {
			_.label.isIdentifier }
		
		// Finds the identifier values (skips empty values)
		val identifiersWithValues = mapValues(result, companyIdentifierMappings)
		
		// If no identifiers were found, won't continue
		if (identifiersWithValues.nonEmpty)
		{
			// Checks whether there already exists data for a company with those identifiers
			val existingCompanyId = identifiersWithValues.findMap { case (label, identifier) =>
				Company.idForIdentifier(label.id, identifier) }
			
			// If no existing company was found, creates a new one
			val companyId = existingCompanyId getOrElse Company.insert(sourceId)
			
			// Creates a company data read event
			val dataRead = Company.insertRead(sourceId, companyId, result.dataOriginTime)
			
			// Finds non-identifier labels & mappings
			val nonIdentifierLabelsWithValues = mapValues(result, companyNonIdentifierMappings)
			
			// Pushes all read data to DB
			val allLabelsWithValues = identifiersWithValues ++ nonIdentifierLabelsWithValues
			Company.insertData(dataRead.id, allLabelsWithValues.map { case (label, value) => label.id -> value })
			
			// Next finds contact identifier data
			val (contactNonIdentifierMappings, contactIdentifierMappings) =
				contactLabelMappings.divideBy { _.label.isIdentifier }
			val contactIdentifiersWithValues = mapValues(result, contactIdentifierMappings)
			
			// If no contact identifiers were found, won't handle the rest of contact data
			if (contactIdentifiersWithValues.nonEmpty)
			{
				// Tries to find an existing contact based on the identifier(s)
				val existingContactId = contactIdentifiersWithValues.findMap { case (label, identifier) =>
					Contact.idForIdentifier(label.id, identifier) }
				
				// If there's no existing contact, creates one
				val contactId = existingContactId getOrElse Contact.insert(sourceId)
				
				// Creates a contact data read event
				val contactDataRead = Contact.insertRead(sourceId, contactId, result.dataOriginTime)
				
				// Finds the rest of the contact mappings, then pushes all data to DB
				val nonIdentifierContactLabelsWithValues = mapValues(result, contactNonIdentifierMappings)
				val allContactLabelsWithValues = contactIdentifiersWithValues ++ nonIdentifierContactLabelsWithValues
				Contact.insertData(contactDataRead.id,
					allContactLabelsWithValues.map { case (label, value) => label.id -> value })
				
				// Checks which roles the contact should be assigned
				val roleIds = mapRoles(result, contactRoleMappings)
				val existingContact = Contact.withId(contactId)
				
				// If for some reason the contact is no longer in DB, won't alter roles
				if (existingContact.isDefined)
				{
					val existingRoleIds = existingContact.get.rolesWithinCompany(companyId).map { _.id }
					val newRoleIds = roleIds -- existingRoleIds
					val removedRoleIds = existingRoleIds -- roleIds
					
					// Updates new roles to database, also removes deprecated roles
					Contact.addRolesForContact(contactId, companyId, newRoleIds, sourceId)
					Contact.removeRolesFromContact(contactId, companyId, removedRoleIds.toSeq)
				}
				else
					Log.warning(s"Newly added contact ($contactId) couldn't be found from the database.")
			}
		}
	}
	
	private def mapValues(result: QueryResult, mappings: Traversable[LabelMapping]) =
	{
		val mapped = mappings.map { mapping => mapping.label -> mapping(result) }
		val (success, failed) = mapped.divideBy { _._2.isEmpty }
		
		// If some of the labels couldn't be mapped, logs a warning
		if (failed.nonEmpty)
			Log.warning(s"Failed to map some of the granite results (${failed.map { _._1.id }.mkString(", ")}) for $result")
		
		// Will not include empty values in the results, however
		success.map { case (label, value) => label -> value.get }.filter { _._2.isDefined }
	}
	
	private def mapRoles(result: QueryResult, mappings: Traversable[ContactRoleMapping]) =
	{
		val (notFound, found) = mappings.map { m => m -> m(result) }.divideBy { _._2.isDefined }
		
		// If some of the mappings were not found, logs 'em
		if (notFound.nonEmpty)
			Log.warning(s"Failed to map some of roles (${notFound.map { _._1.roleId }.mkString(", ")}) to granite results $result")
		
		found.filter { _._2.contains(true) }.map { _._1.roleId }.toSet
	}
}
