package ch.database.model

import ch.database.Tables
import ch.model.exception.NoSuchOperatorException
import utopia.flow.generic.ValueConversions._
import ch.model.profiling.condition.{CombinationOperator, PartialSegmentFilterConditionCombo}
import utopia.flow.datastructure.template.{Model, Property}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.factory.FromRowModelFactory

import scala.util.{Failure, Success}

object SegmentFilterConditionCombo extends FromRowModelFactory[PartialSegmentFilterConditionCombo]
{
	// IMPLEMENTED	--------------------
	
	override def apply(model: Model[Property]) = table.requirementDeclaration
		.validate(model).toTry.flatMap { valid =>
			
			val parentId = valid("parent").int.map { Right(_) }.orElse { valid("filter").int.map { Left(_) } }
			if (parentId.isDefined)
			{
				CombinationOperator.fromInt(valid("combinationOperator").getInt).map { operator =>
					Success(PartialSegmentFilterConditionCombo(valid("id").getInt, parentId.get, operator))
					
				}.getOrElse(Failure(new NoSuchOperatorException(s"Cannot find a combination operator for ${valid("combinationOperator")}")))
			}
			else
				Failure(new NoSuchElementException(s"Couldn't find 'parent' or 'filter' from $model"))
		}
	
	override def table = Tables.segmentFilterConditionCombo
	
	
	// OTHER	------------------------
	
	/**
	  * @param filterId Id of direct parent filter
	  * @return A model with only filter id set
	  */
	def withFilterId(filterId: Int) = SegmentFilterConditionCombo(filterId = Some(filterId))
	
	/**
	  * @param parentId Id of direct parent combo
	  * @return A model with only parent id set
	  */
	def withParentId(parentId: Int) = SegmentFilterConditionCombo(parentId = Some(parentId))
}

/**
  * Used for searching & updating segment filter condition combination DB data
  * @author Mikko Hilpinen
  * @since 18.7.2019, v0.1+
  */
case class SegmentFilterConditionCombo(id: Option[Int] = None, filterId: Option[Int] = None, parentId: Option[Int] = None,
									   operator: Option[CombinationOperator] = None)
	extends StorableWithFactory[PartialSegmentFilterConditionCombo]
{
	override def factory = SegmentFilterConditionCombo
	
	override def valueProperties = Vector("id" -> id, "filter" -> filterId, "parent" -> parentId,
		"combinationOperator" -> operator.map { _.toInt })
}
