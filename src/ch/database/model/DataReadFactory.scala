package ch.database.model

import java.time.Instant

import utopia.flow.datastructure.immutable
import utopia.flow.datastructure.immutable.Constant
import utopia.vault.model.immutable.factory.StorableFactoryWithValidation

/**
  * Used for reading data read events from DB
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
trait DataReadFactory[+M] extends StorableFactoryWithValidation[ch.model.DataRead]
{
	// ABSTRACT	----------------------
	
	/**
	  * @return The name of the property that represents this data's target
	  */
	def targetPropertyName: String
	
	/**
	  * Creates a new database model based on provided data
	  * @param id Model's id
	  * @param sourceId Read source identifier
	  * @param targetId Read target identifier
	  * @param dataOriginTime Time when read data was originated / generated
	  * @param created Data read time
	  * @return A new model
	  */
	def apply(id: Option[Int] = None, sourceId: Option[Int] = None, targetId: Option[Int] = None,
			  dataOriginTime: Option[Instant] = None, created: Option[Instant] = None): M
	
	
	// IMPLEMENTED	------------------
	
	override protected def fromValidatedModel(valid: immutable.Model[Constant]) = ch.model.DataRead(valid("id").getInt,
		valid("source").getInt, valid(targetPropertyName).getInt, valid("dataOriginTime").getInstant,
		valid("created").getInstant)
	
	
	// OTHER	----------------------
	
	/**
	  * Creates a new company data read instance ready to be inserted to DB
	  * @param sourceId Read source identifier
	  * @param targetId Read target identifier
	  * @param dataOriginTime Time when read data was originated / generated
	  * @param readTime Data read time
	  * @return A new read model ready to be inserted
	  */
	def forInsert(sourceId: Int, targetId: Int, dataOriginTime: Instant, readTime: Instant = Instant.now()) = apply(None,
		Some(sourceId), Some(targetId), Some(dataOriginTime), Some(readTime))
	
	/**
	  * @param targetId Targeted entity's id
	  * @return A read with specified company id
	  */
	def withTargetId(targetId: Int) = apply(targetId = Some(targetId))
}