package ch.model.scoring

import utopia.flow.datastructure.immutable.Value
import utopia.flow.util.CollectionExtensions._

object RiskFunction
{
	/**
	 * This function picks the first value from those available
	 */
	case object FirstValue extends RiskFunction
	{
		override val id = 1
		override def apply(values: Seq[Value]) = values.findMap { _.double }
	}
	
	/**
	 * This function calculates the average value of items
	 */
	case object Average extends RiskFunction
	{
		val id = 2
		override def apply(values: Seq[Value]) =
		{
			val doubleValues = values.flatMap { _.double }
			if (doubleValues.nonEmpty)
				Some(doubleValues.sum / values.size)
			else
				None
		}
	}
	
	/**
	 * This function calculates average value of items, but treats items after first 0 as 0
	 */
	case object Sequence extends RiskFunction
	{
		override val id = 3
		override def apply(values: Seq[Value]) =
		{
			val doubleValues = values.map { _.getDouble }
			val sequence = doubleValues.takeWhile { _ > 0 }
			if (sequence.nonEmpty)
				Some(sequence.sum / values.size)
			else if (values.exists { _.isDefined })
				Some(0.0)
			else
				None
		}
	}
	
	/**
	 * Currently available function values
	 */
	val values = Vector(FirstValue, Average, Sequence)
	
	/**
	 * @param functionId An id representing a function
	 * @return Function with specified id. None if no such function could be found.
	 */
	def forId(functionId: Int): Option[RiskFunction] = values.find { _.id == functionId }
}

/**
 * Represents different ways of calculating risk scores
 * @author Mikko Hilpinen
 * @since 21.8.2019, v1.1+
 */
sealed trait RiskFunction
{
	/**
	 * @return This function's unique id
	 */
	val id: Int
	
	/**
	 * Calculates a single risk score
	 * @param values Input values
	 * @return Risk score [0, 1] where 0 is lowest (worst) score and 1 is the highest (best) score. None if score
	 *         couldn't be calculated with provided data.
	 */
	def apply(values: Seq[Value]): Option[Double]
}
