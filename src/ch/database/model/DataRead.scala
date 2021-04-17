package ch.database.model

import java.time.Instant

import ch.database.Tables
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.factory.FromValidatedRowModelFactory

object DataRead extends FromValidatedRowModelFactory[ch.model.DataRead]
{
	// IMPLEMENTED	----------------------
	
	override def table = Tables.dataRead
	
	override protected def fromValidatedModel(model: Model[Constant]) = ch.model.DataRead(model("id").getInt,
		model("source").getInt, model("target").getInt, model("dataOriginTime").getInstant, model("created").getInstant)
	
	
	// OTHER	--------------------------
	
	/**
	 * Creates a new data read instance ready to be inserted to DB
	 * @param sourceId Read source id
	 * @param targetId Read target id
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
	
	/**
	 * @param sourceId Id of data read source
	 * @return A model with only source id set
	 */
	def withSourceId(sourceId: Int) = apply(sourceId = Some(sourceId))
}

/**
 * Used for interacting with data read DB data
 * @author Mikko Hilpinen
 * @since 5.10.2019, v2+
 */
case class DataRead(id: Option[Int] = None, sourceId: Option[Int] = None, targetId: Option[Int] = None,
					dataOriginTime: Option[Instant] = None, created: Option[Instant] = None)
	extends StorableWithFactory[ch.model.DataRead]
{
	override def factory = DataRead
	
	override def valueProperties = Vector("id" -> id, "source" -> sourceId,
		"target" -> targetId, "dataOriginTime" -> dataOriginTime, "created" -> created)
}
