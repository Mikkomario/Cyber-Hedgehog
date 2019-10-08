package ch.database

import java.time.Instant

import utopia.flow.generic.ValueConversions._
import ch.model.scoring
import utopia.vault.database.Connection
import utopia.vault.model.immutable.access.SingleAccess
import utopia.vault.sql.{MaxBy, Select, Where}

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
	def latest(implicit connection: Connection) = factory.getMax("created")
	
	
	// IMPLEMENTED	---------------------
	
	override protected def idValue(id: Int) = id
	
	override def factory = model.RiskScoringEvent
	
	
	// OTHER	-------------------------
	
	/**
	 * @param typeId Id of analyzed entity type
	 * @param connection DB Connection
	 * @return Latest risk scoring event for the specified entity type
	 */
	def latestForEntityTypeWithId(typeId: Int)(implicit connection: Connection) =
	{
		connection(Select(table join Algorithm.table, table) +
			Where(model.Algorithm.withTargetTypeId(typeId).toCondition) + MaxBy(table("created"))).parseSingle(factory)
	}
	
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
