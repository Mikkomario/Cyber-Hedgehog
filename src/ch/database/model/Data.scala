package ch.database.model

import utopia.flow.generic.ValueConversions._
import utopia.flow.datastructure.immutable.Value
import utopia.vault.model.immutable.StorableWithFactory

/**
  * Common trait for data DB models
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
trait Data[Repr] extends StorableWithFactory[ch.model.Data]
{
	// ABSTRACT	--------------------
	
	/**
	  * @return This data's unique id
	  */
	def id: Option[Int]
	/**
	  * @return Id of read event associated with this data
	  */
	def readId: Option[Int]
	/**
	  * @return Id of label associated with this data
	  */
	def labelId: Option[Int]
	/**
	  * @return Value of this data
	  */
	def value: Option[Value]
	
	def makeCopy(id: Option[Int] = None, readId: Option[Int] = None, labelId: Option[Int] = None,
						   value: Option[Value] = None): Repr
	
	
	// IMPLEMENTED	----------------
	
	override def valueProperties = Vector("id" -> id, "read" -> readId, "label" -> labelId,
		"value" -> value.map { _.toJSON })
	
	
	// OTHER	-------------------
	
	/**
	  * @param value New value
	  * @return A copy of this data with specified value
	  */
	def withValue(value: Value) = makeCopy(value = Some(value))
}
