package ch.database

import ch.model.scoring
import utopia.vault.database.Connection
import utopia.vault.nosql.access.SingleRowModelAccess

/**
 * Used for accessing individual risk scores in DB
 * @author Mikko Hilpinen
 * @since 25.8.2019, v1.1+
 */
object RiskScore extends SingleRowModelAccess[scoring.RiskScore]
{
	// IMPLEMENTED	--------------------
	
	override def globalCondition = None
	
	override def factory = model.RiskScore
	
	
	// OTHER	------------------------
	
	/**
	 * Inserts a new risk score to DB
	 * @param companyId Id of targeted company
	 * @param score Score for targeted company
	 * @param event Associated scoring event
	 * @param connection DB connection (implicit)
	 * @return The newly inserted score instance
	 */
	def insert(companyId: Int, score: Double, event: scoring.RiskScoringEvent)(implicit connection: Connection) =
	{
		val newId = factory.forInsert(companyId, score, event.id).insert().getInt
		scoring.RiskScore(newId, companyId, score, event)
	}
}
