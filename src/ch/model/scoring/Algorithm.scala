package ch.model.scoring

import java.time.Instant

import ch.model.DataSet

/**
 * An algorithm used for calculating company cyber risk score
 * @author Mikko Hilpinen
 * @since 24.8.2019, v1.1+
 * @param id The unique id of this algorithm version
 * @param created The time when this algorithm version was released
 * @param modifiers The modifiers that form this algorithm
 */
case class Algorithm(id: Int, created: Instant, modifiers: Set[AlgorithmModifier])
{
	/**
	 * Calculates a risk score based on company data
	 * @param data Company data
	 * @return Company risk score
	 */
	def apply(data: DataSet) =
	{
		// Calculates weighted average of all modifier results
		val results = modifiers.toVector.map { _(data) }
		if (results.nonEmpty)
		{
			val sum = results.foldLeft(0.0) { (total, result) => total + result._1 * result._2 }
			val divider = results.map { _._2 }.sum
			
			if (divider != 0)
				sum / divider
			else
				0.0
		}
		else
			0.0
	}
}
