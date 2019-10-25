package ch.model

import utopia.flow.datastructure.immutable.Value

object DataSet
{
	/**
	  * An empty data set
	  */
	val empty = DataSet(Set())
	
	/**
	 * @param data Entity data
	 * @return A data set based on the data
	 */
	def apply(data: Traversable[Data]) = new DataSet(data.map { d => d.label -> d.value }.toSet)
}

/**
  * Used for easily combining multiple labels with associated data
  * @author Mikko Hilpinen
  * @since 18.7.2019, v0.1+
  */
case class DataSet(data: Set[(EntityLabel, Value)])
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
	def ++(other: DataSet) =
	{
		val myLabelIds = data.map { _._1.id }
		val otherLabelIds = other.data.map { _._1.id }
		val inBoth = myLabelIds & otherLabelIds
		val onlyInMe = myLabelIds -- otherLabelIds
		val onlyInOther = otherLabelIds -- myLabelIds
		
		// Will not overwrite data with empty values
		val newSharedData = inBoth.map { id =>
			val otherData = other(id).get
			if (otherData._2.isDefined)
				otherData
			else
				apply(id).get
		}
		
		DataSet(onlyInMe.map { apply(_).get } ++ newSharedData ++ onlyInOther.map { other(_).get })
	}
	
	
	// OTHER	------------------------
	
	/**
	  * Finds a label value
	  * @param labelId Id of targeted label
	  * @return Value for the label. None if no such label was found.
	  */
	def apply(labelId: Int) = data.find { _._1.id == labelId }
	
	/**
	 * @param labelId Searched label's id
	 * @return Whether this data set contains a value (which may be empty) for the specified label
	 */
	def containsLabelWithId(labelId: Int) = data.exists { _._1.id == labelId }
}
