package ch.database.model

import java.time.Instant

import ch.database.Tables
import ch.model.profiling
import ch.model.profiling.ProfilingEvent
import utopia.flow.generic.ValueConversions._
import utopia.flow.datastructure.immutable
import utopia.flow.datastructure.immutable.Constant
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.immutable.factory.StorableFactoryWithValidation

object Profiling extends StorableFactoryWithValidation[ProfilingEvent]
{
	// IMPLEMENTED	-------------------
	
	override def table = Tables.segmentProfiling
	
	override protected def fromValidatedModel(valid: immutable.Model[Constant]) = profiling.ProfilingEvent(valid("id").getInt,
		valid("segment").getInt, valid("filter").getInt, valid("created").getInstant)
	
	
	// OTHER	----------------------
	
	/**
	  * Creates a new profiling model ready to be inserted to DB
	  * @param segmentId Target segment's id
	  * @param filterId Used filter's id
	  */
	def forInsert(segmentId: Int, filterId: Int) = Profiling(None, Some(segmentId), Some(filterId))
	
	/**
	  * @param segmentId Segment identifier
	  * @return Model with only segment id set
	  */
	def withSegmentId(segmentId: Int) = Profiling(segmentId = Some(segmentId))
}

/**
  * Used for searching & updating segment profiling DB data
  * @author Mikko Hilpinen
  * @since 17.7.2019, v0.1+
  */
case class Profiling(id: Option[Int] = None, segmentId: Option[Int] = None, filterId: Option[Int] = None,
					 created: Option[Instant] = None) extends StorableWithFactory[ProfilingEvent]
{
	override def factory = Profiling
	
	override def valueProperties = Vector("id" -> id, "segment" -> segmentId, "filter" -> filterId, "created" -> created)
}
