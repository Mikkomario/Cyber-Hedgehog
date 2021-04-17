package ch.database.model

import utopia.flow.generic.ValueConversions._
import ch.database.Tables
import ch.model.scoring
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.factory.LinkedFactory

object RiskScore extends LinkedFactory[scoring.RiskScore, scoring.RiskScoringEvent]
{
	// IMPLEMENTED	---------------------
	
	override def childFactory = RiskScoringEvent
	
	override def apply(model: Model[Constant], child: scoring.RiskScoringEvent) =
		table.requirementDeclaration.validate(model).toTry.map { valid => scoring.RiskScore(valid("id").getInt,
			valid("target").getInt, valid("score").getDouble, child) }
	
	override def table = Tables.riskScore
	
	
	// OTHER	-------------------------
	
	/**
	 * Creates a new model ready to be inserted to DB
	 * @param targetId Id of scored company
	 * @param score Score for company
	 * @param eventId Id of associated scoring event
	 * @return A new model
	 */
	def forInsert(targetId: Int, score: Double, eventId: Int) = RiskScore(None, Some(targetId), Some(score), Some(eventId))
}

/**
 * Used for editing and searching cyber risk score DB data
 * @author Mikko Hilpinen
 * @since 24.8.2019, v1.1+
 */
case class RiskScore(id: Option[Int] = None, targetId: Option[Int] = None, score: Option[Double] = None,
					 eventId: Option[Int] = None) extends StorableWithFactory[scoring.RiskScore]
{
	override def factory = RiskScore
	
	override def valueProperties = Vector("id" -> id, "target" -> targetId, "score" -> score,
		"event" -> eventId)
}
