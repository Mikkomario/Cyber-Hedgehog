package ch.database.model

import ch.database.Tables
import ch.model.scoring
import ch.util.Log
import utopia.vault.model.immutable.Result
import utopia.vault.model.immutable.factory.FromResultFactory

import scala.util.{Failure, Success}

/**
 * Used for reading cyber risk algorithm data from DB
 * @author Mikko Hilpinen
 * @since 24.8.2019, v1.1+
 */
object Algorithm extends FromResultFactory[scoring.Algorithm]
{
	override def table = Tables.riskAlgorithm
	
	override def joinedTables = AlgorithmModifier.tables
	
	override def apply(result: Result) =
	{
		// Starts by finding the rows for each algorithm version
		result.split(table).flatMap { algorithmResult =>
			
			// Handles the algorithm from the first row, then parses modifiers
			table.requirementDeclaration.validate(algorithmResult.rows.head(table)).toTry match
			{
				case Success(myModel) => Some(scoring.Algorithm(myModel("id").getInt, myModel("created").getInstant,
					AlgorithmModifier(algorithmResult).toSet))
					
				case Failure(error) => Log(error, "Failed to validate algorithm model"); None
			}
		}
	}
}
