package ch.database.model

import utopia.flow.generic.ValueConversions._
import ch.database.Tables
import utopia.vault.model.immutable.Storable

object SegmentContent
{
	/**
	  * Creates a new model ready to be inserted to DB
	  * @param profilingId Id of associated profiling event
	  * @param entityId Id of connected entity
	  */
	def forInsert(profilingId: Int, entityId: Int) = SegmentContent(None, Some(profilingId), Some(entityId))
	
	/**
	  * @param profilingId Id of associated profiling event
	  * @return A model with only profilingId set
	  */
	def withProfilingId(profilingId: Int) = SegmentContent(profilingId = Some(profilingId))
}

/**
  * Used for searching and updating entity segment connections (per profile) in DB
  * @author Mikko Hilpinen
  * @since 19.7.2019, v0.1+
  */
case class SegmentContent(id: Option[Int] = None, profilingId: Option[Int] = None, entityId: Option[Int] = None)
	extends Storable
{
	override def table = Tables.segmentContent
	
	override def valueProperties = Vector("id" -> id, "profiling" -> profilingId,
		"includedEntity" -> entityId)
}
