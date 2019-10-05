package ch.database.model

import utopia.flow.generic.ValueConversions._
import ch.model.DataType
import utopia.vault.model.immutable.StorableWithFactory

/**
  * These models represent data labels of some sort
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
trait DataLabel extends StorableWithFactory[ch.model.DataLabel]
{
	// ABSTRACT	----------------
	
	/**
	  * @return This label's unique identifier
	  */
	def id: Option[Int]
	/**
	  * @return This label's data type
	  */
	def dataType: Option[DataType]
	/**
	  * @return whether this label should be considered an identifier for data owner
	  */
	def isIdentifier: Option[Boolean]
	/**
	  * @return Whether this label's value represents an email address
	  */
	def isEmail: Option[Boolean]
	
	
	// IMPLEMENTED	------------
	
	override def valueProperties = Vector("id" -> id, "dataType" -> dataType.map { _.toInt },
		"isIdentifier" -> isIdentifier, "isEmail" -> isEmail)
}
