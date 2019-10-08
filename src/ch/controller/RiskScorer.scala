package ch.controller

import ch.database.{Algorithm, DataRead, DataReads, Entity, RiskScore, RiskScoringEvent}
import utopia.vault.database.Connection

/**
 * Performs cyber risk scoring
 * @author Mikko Hilpinen
 * @since 25.8.2019, v1.1+
 */
object RiskScorer
{
	/**
	 * Updates cyber risk scoring for entities. Only entities with updated data will be scored. If a new algorithm
	 * version has been released, however, performs scoring for all algorithm targets
	 * @param connection DB connection (implicit)
	 */
	def run()(implicit connection: Connection) =
	{
		// Performs analysis for each entity type (provided there is an algorithm for calculating the score)
		Entity.typeIds.foreach { typeId =>
			// Skips types without algorithm data
			Algorithm.id.ofLatestVersionForTypeWithId(typeId).foreach { currentAlgorithmId =>
				
				// Reads last scoring time and checks which entities' data has been updated since that
				val updateReads =
				{
					val lastScoring = RiskScoringEvent.latestForEntityTypeWithId(typeId)
					
					// Also updates scoring for all target entities whenever algorithm version changes
					if (lastScoring.forall { _.algorithmId != currentAlgorithmId })
						DataReads.forTypeWithId(typeId).latestVersions
					else
						DataReads.forTypeWithId(typeId).latestVersionsAfter(lastScoring.get.created)
				}
				
				if (updateReads.nonEmpty)
				{
					// Updates scoring for all affected entities
					Algorithm.withId(currentAlgorithmId).foreach { algorithm =>
						
						// Saves a new scoring event first
						val newEvent = RiskScoringEvent.insert(currentAlgorithmId)
						
						updateReads.foreach { dataRead =>
							val data = DataRead(dataRead.id).data
							val score = algorithm(data)
							
							// Updates score to DB
							RiskScore.insert(dataRead.targetId, score, newEvent)
						}
					}
				}
			}
		}
	}
}
