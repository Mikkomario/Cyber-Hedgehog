package ch.model.scoring

import ch.model.{EntityLabelGroup, DataSet}

/**
 * Represents a single parameter in cyber risk score algorithm
 * @author Mikko Hilpinen
 * @since 21.8.2019, v1.1+
 * @param id This modifier's unique id
 * @param algorithmId Id of the algorithm this modifier is part of
 * @param source Either label id (left) or label group (right)
 * @param function Function used for calculating result value
 * @param importance Relative importance of this modifier
 * @param backupResult Result provided when function fails or data is not available
 * @param backupImportance Importance of result when backup result is used
 */
case class AlgorithmModifier(id: Int, algorithmId: Int, source: Either[Int, EntityLabelGroup],
							 function: RiskFunction, importance: Int, backupResult: Double, backupImportance: Int)
{
	/**
	 * Performs an analysis over company data
	 * @param data The company data
	 * @return Calculated risk score (parameter) and result importance
	 */
	def apply(data: DataSet) =
	{
		// Finds label id(s) and data associated with those label(s)
		val labelValues = source match
		{
			case Left(labelId) => data(labelId).map { _._2 }.toVector
			case Right(labelGroup) => labelGroup.labelIds.flatMap { data(_).map { _._2 } }
		}
		
		// Tries to use the function, if that doesn't work, provides backup result
		function(labelValues).map { _ -> importance }.getOrElse(backupResult -> backupImportance)
	}
}
