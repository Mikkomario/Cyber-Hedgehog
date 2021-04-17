package ch.database.model

import utopia.flow.generic.ValueConversions._
import ch.database.Tables
import ch.model.scoring
import ch.model.scoring.RiskFunction
import ch.util.Log
import utopia.vault.model.immutable.Result
import utopia.vault.nosql.factory.FromResultFactory
import utopia.vault.sql.JoinType

import scala.util.{Failure, Success}

/**
 * Used for reading algorithm modifier data from DB
 * @author Mikko Hilpinen
 * @since 21.8.2019, v1.1+
 */
object AlgorithmModifier extends FromResultFactory[scoring.AlgorithmModifier]
{
	override def table = Tables.riskScoreModifier
	
	override def joinType = JoinType.Left
	
	override def joinedTables = EntityLabelGroup.tables
	
	override def apply(result: Result) =
	{
		result.grouped(table, joinedTables).toVector.flatMap { case (id, data) =>
			
			val (myRow, otherRows) = data
			table.requirementDeclaration.validate(myRow(table)).toTry match
			{
				case Success(myModel) =>
					
					val functionId = myModel("function").getInt
					val function = RiskFunction.forId(functionId)
					if (function.isEmpty)
					{
						Log.warning(s"No risk function available for id $functionId at algorithm modifier $id")
						None
					}
					else
					{
						val algorithmId = myModel("algorithm").getInt
						val importance = myModel("importance").getInt
						val backupResult = myModel("resultWhenDataNotFound").getDouble
						val backupImportance = myModel("importanceWhenDataNotFound").getInt
						
						val groupId = myModel("sourceLabelGroup").int
						val labelId = myModel("sourceLabel").int
						
						// Either label id or group id must be provided
						if (groupId.isEmpty && labelId.isEmpty)
						{
							Log.warning("Neither group id nor label id provided for algorithm modifier: " + myModel)
							None
						}
						// Case group id specified
						else if (groupId.isDefined)
						{
							// Parses label group model, if one was provided
							val groupModel = otherRows.get(EntityLabelGroup.table).flatMap {
								_.headOption.map { _(EntityLabelGroup.table) }}
							if (groupModel.isEmpty)
							{
								Log.warning(s"Failed to read data for associated company data label group with id ${groupId.get}")
								None
							}
							else
							{
								// Parses group connections from each connection row
								val labelLinks = otherRows.getOrElse(EntityLabelGroupContent.table, Vector())
									// TODO: Handle parsing failures
									.flatMap { linkRow => EntityLabelGroupContent(linkRow).toOption }
								// Parses group data into a completed model and uses it in algorithm model
								EntityLabelGroup(groupId.get, groupModel.get, labelLinks) match
								{
									case Success(group) => Some(scoring.AlgorithmModifier(id.getInt, algorithmId,
										Right(group), function.get, importance, backupResult, backupImportance))
									case Failure(error) => Log(error,
										s"Failed to parse data label group for algorithm modifier $id"); None
								}
							}
						}
						// Case source label specified
						else
							Some(scoring.AlgorithmModifier(id.getInt, algorithmId, Left(labelId.get), function.get,
								importance, backupResult, backupImportance))
					}
					
				case Failure(error) => Log(error, s"Failed to parse AlgorithmModifier from $myRow"); None
			}
		}
	}
}
