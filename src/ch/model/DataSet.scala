package ch.model

import utopia.flow.datastructure.immutable.Value

object DataSet
{
	/**
	  * An empty data set
	  */
	val empty = DataSet(Set())
}

/**
  * Used for easily combining multiple labels with associated data
  * @author Mikko Hilpinen
  * @since 18.7.2019, v0.1+
  */
case class DataSet(data: Set[(DataLabel, Value)])
{
	// COMPUTED	------------------------
	
	/**
	 * @return Whether this data set is empty
	 */
	def isEmpty = data.isEmpty
	
	/**
	 * @return Whether this data contains actual values
	 */
	def nonEmpty = !isEmpty
	
	// IMPLEMENTED	--------------------
	
	override def toString = s"[${data.map { case (label, value) => s"${label.id}='${value.getString}'" }.mkString(", ")}]"
	
	
	// OPERATORS	--------------------
	
	/**
	 * @param other Another data set
	 * @return A combination of these sets
	 */
	def ++(other: DataSet) = DataSet(data ++ other.data)
	
	
	// OTHER	------------------------
	
	/**
	  * Finds a label value
	  * @param labelId Id of targeted label
	  * @return Value for the label. None if no such label was found.
	  */
	def apply(labelId: Int) = data.find { _._1.id == labelId }
}
