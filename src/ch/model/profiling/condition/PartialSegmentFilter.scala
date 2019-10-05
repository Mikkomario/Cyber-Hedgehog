package ch.model.profiling.condition

/**
  * Contains segment filter information without sub-conditions
  * @author Mikko Hilpinen
  * @since 18.7.2019, v0.1+
  * @param id This filter's unique id
  * @param segmentId The id of this filter's target segment
  * @param operator Operator used in this filter
  */
case class PartialSegmentFilter(id: Int, segmentId: Int, operator: CombinationOperator)
{
	/**
	  * Completes this filter by providing child filter conditions
	  * @param children Filter conditions used in this filter
	  * @return A completed segment filter
	  */
	def withChildren(children: Seq[FilterCondition]) = SegmentFilter(id, segmentId, operator, children)
}
