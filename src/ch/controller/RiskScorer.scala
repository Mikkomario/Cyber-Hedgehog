package ch.controller

import ch.database.{Algorithm, Entities, Entity, RiskScore, RiskScoringEvent}
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
			//println(s"Scoring type $typeId")
			// Skips types without algorithm data
			Algorithm.id.ofLatestVersionForTypeWithId(typeId).foreach { currentAlgorithmId =>
				
				// Reads last scoring time and checks which entities' data has been updated since that
				val updatedEntityIds =
				{
					val lastScoring = RiskScoringEvent.latestForEntityTypeWithId(typeId)
					//println(s"Using algorithm $currentAlgorithmId which was scored ${lastScoring.map {
					//	_.created.toString }.getOrElse("never")}")
					
					// Also updates scoring for all target entities whenever algorithm version changes
					if (lastScoring.forall { _.algorithmId != currentAlgorithmId })
						Entities.ids.forTypeWithId(typeId).get
					else
						Entities.ids.forTypeWithId(typeId).updatedAfter(lastScoring.get.created)
				}
				
				//println(s"Found following updated entities: ${updatedEntityIds.mkString(", ")}")
				if (updatedEntityIds.nonEmpty)
				{
					// Updates scoring for all affected entities
					Algorithm.withId(currentAlgorithmId).foreach { algorithm =>
						
						// Saves a new scoring event first
						val newEvent = RiskScoringEvent.insert(currentAlgorithmId)
						
						updatedEntityIds.foreach { targetId =>
							val data = Entity(targetId).latestData
							val score = algorithm(data)
							//println(s"Scored $targetId: $score")
							
							// Updates score to DB
							RiskScore.insert(targetId, score, newEvent)
						}
					}
				}
			}
		}
	}
}
