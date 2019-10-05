package ch.mailchimp.database.model

import java.time.Instant

import utopia.flow.generic.ValueConversions._
import ch.mailchimp.database.Tables
import utopia.flow.datastructure.immutable
import utopia.flow.datastructure.immutable.Constant
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.immutable.factory.StorableFactoryWithValidation

object SegmentUpdateEvent extends StorableFactoryWithValidation[ch.mailchimp.model.SegmentUpdateEvent]
{
	// IMPLEMENTED	--------------------
	
	override def table = Tables.segmentUpdateEvent
	
	override protected def fromValidatedModel(valid: immutable.Model[Constant]) = ch.mailchimp.model.SegmentUpdateEvent(
		valid("id").getInt, valid("segment").getInt, valid("profiling").getInt, valid("created").getInstant)
	
	
	// OTHER	------------------------
	
	/**
	  * Creates a new model ready to be inserted
	  * @param segmentId Updated segment's id
	  * @param profilingId The id of the profiling that was used
	  */
	def forInsert(segmentId: Int, profilingId: Int) = SegmentUpdateEvent(None, Some(segmentId), Some(profilingId))
	
	/**
	  * @param segmentId Target segment's id
	  * @return Model with only segment id set
	  */
	def withSegmentId(segmentId: Int) = SegmentUpdateEvent(segmentId = Some(segmentId))
}

/**
  * Used for searching & updating segment update event DB data
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
case class SegmentUpdateEvent(id: Option[Int] = None, segmentId: Option[Int] = None, profilingId: Option[Int] = None,
							  created: Option[Instant] = None)
	extends StorableWithFactory[ch.mailchimp.model.SegmentUpdateEvent]
{
	override def factory = SegmentUpdateEvent
	
	override def valueProperties = Vector("id" -> id, "segment" -> segmentId, "profiling" -> profilingId, "created" -> created)
}
