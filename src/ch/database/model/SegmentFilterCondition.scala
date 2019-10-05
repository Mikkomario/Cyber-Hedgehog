package ch.database.model

import ch.database.Tables
import ch.model.profiling.condition.ConditionOperator
import utopia.flow.generic.ValueConversions._
import ch.model.profiling.condition
import ch.model.exception.NoSuchOperatorException
import ch.util.Log
import utopia.flow.datastructure.template.{Model, Property}
import utopia.flow.parse.JSONReader
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.immutable.factory.StorableFactory

import scala.util.{Failure, Success}

object SegmentFilterCondition extends StorableFactory[ch.model.profiling.condition.SegmentFilterCondition]
{
	// IMPLEMENTED	--------------------
	
	override def apply(model: Model[Property]) =
	{
		table.requirementDeclaration.validate(model).toTry.flatMap { valid =>
			
			val parentId = valid("parent").int.map { Right(_) }.orElse { valid("filter").int.map { Left(_) } }
			if (parentId.isDefined)
			{
				valid("operator").int.flatMap(ConditionOperator.fromInt).map { operator =>
					
					val value = valid("value").string.flatMap { json => JSONReader(json) match
						{
							case Success(parsed) => Some(parsed)
							case Failure(error) => Log(error, s"Failed to parse value for condition $model"); None
						}
					}
					
					Success(condition.SegmentFilterCondition(valid("id").getInt, parentId.get, valid("label").getInt,
						operator, value))
					
				}.getOrElse(Failure(new NoSuchOperatorException(s"Couldn't parse operator from $model")))
			}
			else
				Failure(new NoSuchElementException(s"Couldn't find neither 'parent' nor 'filter' from $model"))
		}
	}
	
	override def table = Tables.segmentFilterCondition
	
	
	// OTHER	-------------------------
	
	/**
	  * @param filterId Id of parent filter
	  * @return A model with only filter id set
	  */
	def withFilterId(filterId: Int) = SegmentFilterCondition(filterId = Some(filterId))
	
	/**
	  * @param parentId Id of parent condition combo
	  * @return A model with only parent id set
	  */
	def withParentId(parentId: Int) = SegmentFilterCondition(parentId = Some(parentId))
}

/**
  * Used for searching & updating segment filter condition DB data
  * @author Mikko Hilpinen
  * @since 17.7.2019, v0.1+
  */
case class SegmentFilterCondition(id: Option[Int] = None, filterId: Option[Int] = None, parentId: Option[Int] = None,
								  operator: Option[ConditionOperator] = None)
	extends StorableWithFactory[ch.model.profiling.condition.SegmentFilterCondition]
{
	override def factory = SegmentFilterCondition
	
	override def valueProperties = Vector("id" -> id, "filter" -> filterId, "parent" -> parentId,
		"operator" -> operator.map { _.toInt })
}
