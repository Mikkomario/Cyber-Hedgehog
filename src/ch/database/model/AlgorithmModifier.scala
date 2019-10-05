package ch.database.model

import utopia.flow.generic.ValueConversions._
import ch.database.Tables
import ch.model.scoring
import ch.model.scoring.RiskFunction
import ch.util.Log
import utopia.vault.model.immutable.Result
import utopia.vault.model.immutable.factory.FromResultFactory

import scala.util.{Failure, Success}

/**
 * Used for reading algorithm modifier data from DB
 * @author Mikko Hilpinen
 * @since 21.8.2019, v1.1+
 */
object AlgorithmModifier extends FromResultFactory[scoring.AlgorithmModifier]
{
	override def table = Tables.riskScoreModifier
	
	override def joinedTables = CompanyDataLabelGroup.tables
	
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
							val groupModel = otherRows.get(CompanyDataLabelGroup.table).flatMap {
								_.headOption.map { _(CompanyDataLabelGroup.table) }}
							if (groupModel.isEmpty)
							{
								Log.warning(s"Failed to read data for associated company data label group with id ${groupId.get}")
								None
							}
							else
							{
								val labelLinks = otherRows.getOrElse(CompanyDataLabelGroupContent.table, Vector()).flatMap { linkRow =>
									CompanyDataLabelGroupContent(linkRow) }
								CompanyDataLabelGroup(groupId.get, groupModel.get, labelLinks) match
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
