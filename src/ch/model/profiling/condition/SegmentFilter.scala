package ch.model.profiling.condition
import ch.model.DataSet

/**
  * This segment filter contains all information necessary for filtering data
  * @author Mikko Hilpinen
  * @since 18.7.2019, v0.1+
  */
case class SegmentFilter(id: Int, segmentId: Int, operator: CombinationOperator, children: Seq[FilterCondition])
	extends FilterCondition
{
	override def apply(data: DataSet) = operator(children.map { _(data) })
	override def toString = s"$operator(${children.mkString(", ")})"
}
