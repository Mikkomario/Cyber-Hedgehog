package ch.model.profiling.condition
import ch.model.DataSet
import utopia.flow.datastructure.immutable.Value

/**
  * Represents a single condition for a segment filter
  * @author Mikko Hilpinen
  * @since 17.7.2019, v0.1+
  * @param id This condition's unique index
  * @param parentId Either id of parent filter (left) or id of parent combo condition
  */
case class SegmentFilterCondition(id: Int, parentId: Either[Int, Int], labelId: Int, operator: ConditionOperator,
								  value: Value)
	extends SegmentFilterConditionPart with FilterCondition
{
	// Has to convert values to the same data type
	override def apply(data: DataSet) =
	{
		// println(s"Tests $labelId $operator '${value.getString}'? Data: $data")
		/*val result =*/ data(labelId).exists { case (label, readValue) => operator(readValue,
			value.withType(label.dataType.flowType)) }
		// println(s"Result: $result")
		// result
	}
	
	override def toString = s"$labelId$operator'${value.getString}'"
}
