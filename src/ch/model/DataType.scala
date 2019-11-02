package ch.model

import utopia.flow.generic

object DataType
{
	/**
	  * A representation of String data type
	  */
	case object StringType extends DataType
	{
		override def toInt = 1
		override def flowType = generic.StringType
	}
	
	/**
	  * A representation of Boolean data type
	  */
	case object BooleanType extends DataType
	{
		override def toInt = 2
		override def flowType = generic.BooleanType
	}
	
	
	// ATTRIBUTES	---------------
	
	/**
	  * All recorded data types
	  */
	val values = Vector(StringType, BooleanType)
	
	
	// OTHER	-------------------
	
	/**
	  * Finds a data type matching provided integer representation
	  * @param typeInt An integer representing a data type
	  * @return The represented data type. None if the integer didn't match any data type
	  */
	def forInt(typeInt: Int) = values.find { _.toInt == typeInt }
}

/**
  * Enumeration for different data types used in company data
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
sealed trait DataType extends Equals
{
	/**
	  * @return An integer representation of this data type
	  */
	def toInt: Int
	
	/**
	  * @return The flow version of this data type
	  */
	def flowType: utopia.flow.generic.DataType
}
