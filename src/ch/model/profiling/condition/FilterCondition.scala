package ch.model.profiling.condition

import ch.model.DataSet

/**
  * These objects can be used as a condition for data filtering
  * @author Mikko Hilpinen
  * @since 18.7.2019, v0.1+
  */
trait FilterCondition
{
	// ABSTRACT	----------------
	
	/**
	  * Returns whether this condition is fulfilled with specified data
	  * @param data Data
	  */
	def apply(data: DataSet): Boolean
}
