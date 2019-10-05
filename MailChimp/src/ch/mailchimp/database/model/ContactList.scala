package ch.mailchimp.database.model

import utopia.flow.util.CollectionExtensions._
import ch.mailchimp.database.Tables
import ch.util.Log
import utopia.vault.model.immutable.Result
import utopia.vault.model.immutable.factory.FromResultFactory

import scala.util.{Failure, Success}

/**
  * Used for reading contact list data from DB
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object ContactList extends FromResultFactory[ch.mailchimp.model.ContactList]
{
	override def table = Tables.contactList
	
	override def joinedTables = ContactSegment.tables ++ MergeField.tables
	
	override def apply(result: Result) =
	{
		// Groups rows based on list id
		result.rows.groupBy { _.indexForTable(table).getInt }.toVector.flatMap
		{
			case (id, rows) =>
			
				// Parses model data
				table.requirementDeclaration.validate(rows.head(table)).toTry match
				{
					case Success(valid) =>
						
						// Parses segments & merge fields
						val segments = rows.filter { _.containsDataForTable(ContactSegment.table) }.distinctBy {
							_.indexForTable(ContactSegment.table).getInt }.flatMap { ContactSegment(_) }.toVector
						val mergeFields = rows.filter { _.containsDataForTable(MergeField.table) }.distinctBy {
							_.indexForTable(MergeField.table).getInt }.flatMap { MergeField(_) }.toVector
						
						Some(ch.mailchimp.model.ContactList(id, valid("mailChimpListId").getString, segments, mergeFields))
					
					case Failure(error) => Log(error, s"Failed to parse ContactList from ${rows.head}"); None
				}
		}
	}
}
