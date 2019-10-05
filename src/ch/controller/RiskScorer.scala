package ch.controller

import ch.database.{Algorithm, Company, RiskScore, RiskScoringEvent}
import utopia.vault.database.Connection

/**
 * Performs company cyber risk scoring
 * @author Mikko Hilpinen
 * @since 25.8.2019, v1.1+
 */
object RiskScorer
{
	/**
	 * Updates cyber risk scoring for companies. Only companies with updated data will be scored. If a new algorithm
	 * version has been released, however, performs scoring for all companies
	 * @param connection DB connection (implicit)
	 */
	def run()(implicit connection: Connection) =
	{
		// Won't work without algorithm data in DB
		Algorithm.id.last.foreach { currentAlgorithmId =>
			
			// Reads last scoring time and checks which companies' data has been updated since that
			val updateReads =
			{
				val lastScoring = RiskScoringEvent.last
				
				// Also updates scoring for all companies whenever algorithm version changes
				if (lastScoring.forall { _.algorithmId != currentAlgorithmId })
					Company.lastReads
				else
					Company.lastReadsAfter(lastScoring.get.created)
			}
			
			if (updateReads.nonEmpty)
			{
				// Updates scoring for all affected companies
				Algorithm.withId(currentAlgorithmId).foreach { algorithm =>
					
					// Saves a new scoring event first
					val newEvent = RiskScoringEvent.insert(currentAlgorithmId)
					
					updateReads.foreach { dataRead =>
						val data = Company.readData(dataRead.id)
						val score = algorithm(data)
						
						// Updates score to DB
						RiskScore.insert(dataRead.targetId, score, newEvent)
					}
				}
			}
		}
	}
}
