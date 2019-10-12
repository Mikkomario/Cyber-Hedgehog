package ch.granite.controller

import utopia.flow.util.CollectionExtensions._
import ch.database.{DataReads, Entities, Entity, EntityTypes}
import ch.granite.model.{Granite, LabelMapping, LinkTypeMapping, QueryResult}
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
	/**
	 * Handles results read from a Granite form
	 * @param result Read form response
	 * @param mappings Granite field & option to label mappings
	 * @param linkTypeMappings Link type mappings
	 * @param connection DB connection
	 */
	def apply(result: QueryResult, mappings: Traversable[LabelMapping],
			   linkTypeMappings: Traversable[LinkTypeMapping])(implicit connection: Connection) =
	{
		// First reads and saves data for each supported entity type, saves results (type id -> entity id)
		val readEntities = EntityTypes.ids.all.flatMap { typeId =>
			
			// Starts by finding correct mappings and reading provided identifier values (empty values not included)
			val (regularMappings, identifierMappings) = mappings.filter { _.label.targetEntityTypeId == typeId }
				.divideBy { _.label.isIdentifier }
			val identifierData = readValues(result, identifierMappings)
			
			// Only continues if identifier data was found
			if (identifierData.nonEmpty)
			{
				// Checks whether there already exists data for an entity with those identifiers
				val existingTargetId = identifierData.data.findMap { case (label, idValue) =>
					Entity.id.forIdentifier(label.id, idValue) }
				
				// If no existing company was found, creates a new one
				val targetId = existingTargetId getOrElse Entity.insert(typeId, Granite.id).id
				
				// Creates a company data read event
				val dataRead = DataReads.insert(Granite.id, targetId, result.dataOriginTime)
				
				// Finds non-identifier values
				val regularData = readValues(result, regularMappings)
				
				// Pushes all read data to DB
				val allData = identifierData ++ regularData
				Entities.data.insert(dataRead.id, targetId, allData.data.map { case (label, value) => label.id -> value })
				
				Some(typeId -> targetId)
			}
			else
				None
		}.toMap
		
		// Finishes by linking read data together using link type mappings
		linkTypeMappings.foreach { mapping =>
			val isLinked = mapping(result)
			if (isLinked.isEmpty)
				Log.warning(s"Couldn't find link type mapping ${mapping.option} from result $result")
			else if (isLinked.get)
			{
				// Finds the linked items, if there are any
				val originId = readEntities.get(mapping.originTypeId)
				val targetId = readEntities.get(mapping.targetTypeId)
				
				if (originId.isDefined && targetId.isDefined)
					Entities.links.insert(originId.get, targetId.get, Granite.id, mapping.linkTypeId)
			}
		}
	}
	
	private def readValues(result: QueryResult, mappings: Traversable[LabelMapping]) =
	{
		val mapped = mappings.map { mapping => mapping -> mapping(result) }
		val (success, _) = mapped.divideBy { _._2.isEmpty }
		
		/* TODO: Add some sort of error handling, but remember that some option mappings are supposed to return an empty value
		// If some of the labels couldn't be mapped, logs a warning
		if (failed.nonEmpty)
		{
			val trulyFailed = failed.filter { _._1. }
			Log.warning(s"Failed to map some of the granite results (${failed.map { case (mapping, _) => mapping }.toSet.mkString(", ")}) for $result")
		}*/
		
		// Will not include empty values in the results, however
		DataSet(success.map { case (mapping, value) => mapping.label -> value.get }.filter { _._2.isDefined }.toSet)
	}
}
