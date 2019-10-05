package ch.database.model

import java.time.Instant

import utopia.flow.generic.ValueConversions._
import utopia.vault.model.immutable.StorableWithFactory

/**
  * Common trait for data read db model implementations, which are used for interacting with data read DB data
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
trait DataRead extends StorableWithFactory[ch.model.DataRead]
{
	// ABSTRACT	----------------
	
	/**
	  * @return This read's unique id
	  */
	def id: Option[Int]
	/**
	  * @return Id of this read's origin source
	  */
	def sourceId: Option[Int]
	/**
	  * @return Id of this read's target
	  */
	def targetId: Option[Int]
	/**
	  * @return Time when data was originated
	  */
	def dataOriginTime: Option[Instant]
	/**
	  * @return Time when data was read
	  */
	def created: Option[Instant]
	
	/**
	  * @return Name of "targetId" property in database
	  */
	def targetPropertyName: String
	
	
	// IMPLEMENTED	-----------
	
	override def valueProperties = Vector("id" -> id, "source" -> sourceId, targetPropertyName -> targetId,
		"dataOriginTime" -> dataOriginTime, "created" -> created)
}