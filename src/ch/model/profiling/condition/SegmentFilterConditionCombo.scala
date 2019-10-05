package ch.model.profiling.condition
import ch.model.DataSet

/**
  * Used for combining multiple individual conditions
  * @author Mikko
  * @since 18.7.2019, v0.1+
  */
case class SegmentFilterConditionCombo(id: Int, parentId: Either[Int, Int], operator: CombinationOperator,
									   children: Seq[FilterCondition])
	extends SegmentFilterConditionPart with FilterCondition
{
	override def apply(data: DataSet) = operator(children.map { _(data) })
	override def toString = s"$operator(${children.mkString(", ")})"
}
