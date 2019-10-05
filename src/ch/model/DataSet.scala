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
	/**
	  * Finds a label value
	  * @param labelId Id of targeted label
	  * @return Value for the label. None if no such label was found.
	  */
	def apply(labelId: Int) = data.find { _._1.id == labelId }
	
	override def toString = s"[${data.map { case (label, value) => s"${label.id}='${value.getString}'" }.mkString(", ")}]"
}
