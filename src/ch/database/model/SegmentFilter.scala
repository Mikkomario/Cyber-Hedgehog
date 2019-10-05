package ch.database.model

import java.time.Instant

import ch.database.Tables
import utopia.flow.generic.ValueConversions._
import ch.model.profiling.condition.{CombinationOperator, PartialSegmentFilter}
import ch.model.exception.NoSuchOperatorException
import utopia.flow.datastructure.template.{Model, Property}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.immutable.factory.StorableFactory

import scala.util.{Failure, Success}

object SegmentFilter extends StorableFactory[PartialSegmentFilter]
{
	// IMPLEMENTED	---------------
	
	override def apply(model: Model[Property]) = table.requirementDeclaration.validate(model).toTry.flatMap { valid =>
		val operator = CombinationOperator.fromInt(valid("combinationOperator").getInt)
		if (operator.isEmpty)
			Failure(new NoSuchOperatorException(s"Couldn't recognize operator ${valid("combinationOperator").getInt}"))
		else
			Success(PartialSegmentFilter(valid("id").getInt, valid("segment").getInt, operator.get))
	}
	
	override def table = Tables.segmentFilter
	
	
	// OTHER	-------------------
	
	/**
	  * @param segmentId Target segment's id
	  * @return A model with only segment id set
	  */
	def withSegmentId(segmentId: Int) = SegmentFilter(segmentId = Some(segmentId))
}

/**
  * Used for searching & updating segment filter DB data
  * @author Mikko Hilpinen
  * @since 18.7.2019, v0.1+
  */
case class SegmentFilter(id: Option[Int] = None, segmentId: Option[Int] = None,
						 operator: Option[CombinationOperator] = None, created: Option[Instant] = None)
	extends StorableWithFactory[PartialSegmentFilter]
{
	override def factory = SegmentFilter
	
	override def valueProperties = Vector("id" -> id, "segment" -> segmentId,
		"combinationOperator" -> operator.map { _.toInt }, "created" -> created)
}
