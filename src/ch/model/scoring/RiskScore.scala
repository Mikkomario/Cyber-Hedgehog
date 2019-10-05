package ch.model.scoring

/**
 * Represents a single company's cyber risk score / index at a specific point in time
 * @author Mikko Hilpinen
 * @since 24.8.2019, v1.1+
 * @param id The unique id of this score
 * @param companyId Id of targeted company
 * @param score The company's risk score. Reference values are [0, 1] where 0 is the worst score and 1 is the best.
 * @param event The scoring event that generated this score
 */
case class RiskScore(id: Int, companyId: Int, score: Double, event: RiskScoringEvent)
