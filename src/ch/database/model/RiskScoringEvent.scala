package ch.database.model

import java.time.Instant

import utopia.flow.generic.ValueConversions._
import ch.database.Tables
import ch.model.scoring
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.immutable.factory.StorableFactoryWithValidation

object RiskScoringEvent extends StorableFactoryWithValidation[scoring.RiskScoringEvent]
{
	// IMPLEMENTED	---------------------
	
	override def table = Tables.riskScoringEvent
	
	override protected def fromValidatedModel(model: Model[Constant]) = scoring.RiskScoringEvent(model("id").getInt,
		model("algorithm").getInt, model("created").getInstant)
	
	
	// OTHER	-------------------------
	
	/**
	 * Creates a new risk scoring event ready to be inserted into DB
	 * @param algorithmId Id of algorithm used for the scoring
	 * @param created Scoring timestamp
	 * @return A new model
	 */
	def forInsert(algorithmId: Int, created: Instant = Instant.now) = RiskScoringEvent(None, Some(algorithmId),
		Some(created))
}

/**
 * Used for searching and editing risk scoring event DB data
 * @author Mikko Hilpinen
 * @since 24.8.2019, v1.1+
 */
case class RiskScoringEvent(id: Option[Int] = None, algorithmId: Option[Int] = None, created: Option[Instant] = None)
	extends StorableWithFactory[scoring.RiskScoringEvent]
{
	override def factory = RiskScoringEvent
	
	override def valueProperties = Vector("id" -> id, "algorithm" -> algorithmId, "created" -> created)
}
