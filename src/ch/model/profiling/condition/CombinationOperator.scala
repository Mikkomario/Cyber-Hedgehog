package ch.model.profiling.condition

/**
  * Used for combining multiple conditions together logically
  * @author Mikko Hilpinen
  * @since 18.7.2019, v0.1+
  */
sealed trait CombinationOperator
{
	/**
	  * @return An integer representation of this operator
	  */
	def toInt: Int
	
	/**
	  * Combines multiple condition results into a single result
	  * @param values Condition results
	  * @return A combination of the results
	  */
	def apply(values: Seq[Boolean]): Boolean
}

object CombinationOperator
{
	/**
	  * Returns true when all conditions return true
	  */
	case object AllOp extends CombinationOperator
	{
		override def toInt = 1
		override def apply(values: Seq[Boolean]) = values.forall { b => b }
		override def toString = "ALL"
	}
	
	/**
	  * Returns true when one or more conditions return true
	  */
	case object AnyOp extends CombinationOperator
	{
		override def toInt = 2
		override def apply(values: Seq[Boolean]) = values.contains(true)
		override def toString = "ANY"
	}
	
	/**
	  * Returns true when one or more conditions return false
	  */
	case object NotAllOp extends CombinationOperator
	{
		override def toInt = 3
		override def apply(values: Seq[Boolean]) = values.contains(false)
		override def toString = "NOT ALL"
	}
	
	/**
	  * Returns true when all conditions return false
	  */
	case object NoneOp extends CombinationOperator
	{
		override def toInt = 4
		override def apply(values: Seq[Boolean]) = values.forall { b => !b }
		override def toString = "NONE"
	}
	
	/**
	  * All operator options
	  */
	val values = Vector(AllOp, AnyOp, NotAllOp, NoneOp)
	
	/**
	  * Parses an operator from an integer representation
	  * @param opInt An integer representing an operator
	  * @return Matching operator or None if integer didn't match any operator
	  */
	def fromInt(opInt: Int) = values.find { _.toInt == opInt }
}