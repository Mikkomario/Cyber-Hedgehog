package ch.model.profiling.condition

import utopia.flow.util.CollectionExtensions._

/**
  * A common trait for all elements used in segment filter conditions
  * @author Mikko Hilpinen
  * @since 18.7.2019, v0.1+
  */
trait SegmentFilterConditionPart
{
	// ABSTRACT	------------------
	
	/**
	  * @return This part's unique id
	  */
	def id: Int
	/**
	  * @return Either id of this part's direct parent filter (left) or this part's direct parent combo (right)
	  */
	def parentId: Either[Int, Int]
	
	
	// COMPUTED	------------------
	
	/**
	  * @return Id of this condition's direct parent filter. None if not directly under a filter.
	  */
	def parentFilterId = parentId.leftOption
	
	/**
	  * @return Id of this condition's parent combo condition. None if not part of a combo condition.
	  */
	def parentComboId = parentId.toOption
}
