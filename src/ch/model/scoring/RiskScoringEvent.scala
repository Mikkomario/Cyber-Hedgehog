package ch.model.scoring

import java.time.Instant

/**
 * Represents a point in time when cyber risk analysis / scoring was performed
 * @author Mikko Hilpinen
 * @since 24.8.2019, v1.1+
 * @param id This unique id of this event
 * @param algorithmId The id of the algorithm used for calculating the scores
 * @param created The time when the cyber risk scores were calculated
 */
case class RiskScoringEvent(id: Int, algorithmId: Int, created: Instant)
