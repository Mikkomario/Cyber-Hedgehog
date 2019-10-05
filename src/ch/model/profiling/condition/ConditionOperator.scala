package ch.model.profiling.condition

import utopia.flow.datastructure.immutable.Value

/**
  * Lists supported condition operators
  * @author Mikko Hilpinen
  * @since 17.7.2019, v0.1+
  */
sealed trait ConditionOperator
{
	/**
	  * @return This operator's integer representation
	  */
	def toInt: Int
	/**
	  * Compares two values
	  * @param first The first value
	  * @param second The second value
	  * @return Result of this operation on the two values
	  */
	def apply(first: Value, second: Value): Boolean
}

object ConditionOperator
{
	/**
	  * Equals returns true when two values are equal
	  */
	case object Equals extends ConditionOperator
	{
		override def toInt = 1
		override def apply(first: Value, second: Value) = first == second
		override def toString = "="
	}
	
	/**
	  * NotEquals returns true when two values are not equal
	  */
	object NotEquals extends ConditionOperator
	{
		override def toInt = 2
		override def apply(first: Value, second: Value) = first != second
		override def toString = "!="
	}
	
	/**
	  * All operator value options
	  */
	val values = Vector(Equals, NotEquals)
	
	/**
	  * Converts integer to operation
	  * @param operatorInt An integer representing an operation
	  * @return Operation represented by the integer. None if the integer didn't represent any operator.
	  */
	def fromInt(operatorInt: Int) = values.find { _.toInt == operatorInt }
}