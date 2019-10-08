package ch.granite.controller

import utopia.flow.util.CollectionExtensions._
import ch.database.{Company, Contact, DataRead, Entities, Entity, EntityTypes}
import ch.granite.model.{ContactRoleMapping, FieldLabelMapping, Granite, LabelMapping, LabelMappingOld, LinkTypeMapping, OptionLabelMapping, QueryResult}
import ch.model.DataSet
import ch.util.Log
import utopia.vault.database.Connection

/**
  * Handles incoming query results by saving data to DB
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
object ResultHandler
{
	def apply2(result: QueryResult, mappings: Traversable[LabelMapping],
			   linkTypeMappings: Traversable[LinkTypeMapping])(implicit connection: Connection) =
	{
		// First reads and saves data for each supported entity type, saves results (type id -> entity id)
		val readEntities = EntityTypes.ids.all.flatMap { typeId =>
			
			// Starts by finding correct mappings and reading provided identifier values (empty values not included)
			val (regularMappings, identifierMappings) = mappings.filter { _.label.targetEntityTypeId == typeId }
				.divideBy { _.label.isIdentifier }
			val identifierData = readValues(result, mappings)
			
			// Only continues if identifier data was found
			if (identifierData.nonEmpty)
			{
				// Checks whether there already exists data for an entity with those identifiers
				val existingTargetId = identifierData.data.findMap { case (label, idValue) =>
					Entity.id.forIdentifier(label.id, idValue) }
				
				// If no existing company was found, creates a new one
				val targetId = existingTargetId getOrElse Entity.insert(typeId, Granite.id).id
				
				// Creates a company data read event
				val dataRead = DataRead.insert(Granite.id, targetId, result.dataOriginTime)
				
				// Finds non-identifier values
				val regularData = readValues(result, regularMappings)
				
				// Pushes all read data to DB
				val allData = identifierData ++ regularData
				Entities.data.insert(dataRead.id, allData.data.map { case (label, value) => label.id -> value })
				
				Some(typeId -> targetId)
			}
			else
				None
		}.toMap
		
		// Finishes by linking read data together using link type mappings
		// TODO: Continue
	}
	
	private def readValues(result: QueryResult, mappings: Traversable[LabelMapping]) =
	{
		val mapped = mappings.map { mapping => mapping.label -> mapping(result) }
		val (success, failed) = mapped.divideBy { _._2.isEmpty }
		
		// If some of the labels couldn't be mapped, logs a warning
		if (failed.nonEmpty)
			Log.warning(s"Failed to map some of the granite results (${failed.map { _._1.id }.mkString(", ")}) for $result")
		
		// Will not include empty values in the results, however
		DataSet(success.map { case (label, value) => label -> value.get }.filter { _._2.isDefined }.toSet)
	}
	
	/**
	  * Handles the result read from Granite API, saving new data to DB
	  * @param result The read result
	  * @return Operation result (may contain failure)
	  */
	def apply(result: QueryResult, companyLabelMappings: Traversable[LabelMappingOld],
			  contactLabelMappings: Traversable[LabelMappingOld], contactRoleMappings: Traversable[ContactRoleMapping])
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
	
	private def mapValues(result: QueryResult, mappings: Traversable[LabelMappingOld]) =
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
