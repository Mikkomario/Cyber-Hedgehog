package ch.database

import java.time.Instant

import utopia.flow.generic.ValueConversions._
import ch.model.scoring
import utopia.vault.database.Connection
import utopia.vault.model.immutable.access.SingleAccess

/**
 * Used for accessing individual risk scoring event items
 * @author Mikko Hilpinen
 * @since 25.8.2019, v1.1+
 */
object RiskScoringEvent extends SingleAccess[Int, scoring.RiskScoringEvent]
{
	// COMPUTED	-------------------------
	
	/**
	 * @param connection Database connection (implicit)
	 * @return The latest risk scoring event
	 */
	def last(implicit connection: Connection) = factory.getMax("created")
	
	
	// IMPLEMENTED	---------------------
	
	override protected def idValue(id: Int) = id
	
	override def factory = model.RiskScoringEvent
	
	
	// OTHER	-------------------------
	
	/**
	 * Inserts a new risk scoring event to DB
	 * @param algorithmId Id of algorithm version used
	 * @param connection DB connection (implicit)
	 * @return Newly inserted event
	 */
	def insert(algorithmId: Int)(implicit connection: Connection) =
	{
		val creationTime = Instant.now
		val newId = model.RiskScoringEvent.forInsert(algorithmId, creationTime).insert().getInt
		scoring.RiskScoringEvent(newId, algorithmId, creationTime)
	}
}
