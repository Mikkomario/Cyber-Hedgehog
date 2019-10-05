package ch.model.profiling.condition

/**
  * Represents a segment filter condition combo before model data has been attached
  * @author Mikko Hilpinen
  * @since 18.7.2019, v0.1+
  * @param id This combo's unique id
  * @param parentId Either id of this combo's direct parent filter (left) or id of this combo's direct parent combo (right)
  */
case class PartialSegmentFilterConditionCombo(id: Int, parentId: Either[Int, Int], operator: CombinationOperator)
	extends SegmentFilterConditionPart
{
	/**
	  * Attaches missing child-condition information to this combo
	  * @param children Child conditions
	  * @return A complete filter condition combo
	  */
	def withChildren(children: Seq[FilterCondition]) = SegmentFilterConditionCombo(id, parentId, operator, children)
}
