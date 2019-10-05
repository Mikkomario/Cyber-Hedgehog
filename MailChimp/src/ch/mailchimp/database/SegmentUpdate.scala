package ch.mailchimp.database

import ch.mailchimp.database.model.SegmentUpdateEvent
import utopia.vault.database.Connection

/**
  * Used for interacting with Contact Segment DB data
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object SegmentUpdate
{
	/**
	  * Inserts a new segment update event to the database
	  * @param segmentId Updated segment's id
	  * @param connection DB connection
	  * @return Generated event id
	  */
	def insertEvent(segmentId: Int, profilingId: Int)(implicit connection: Connection) =
		SegmentUpdateEvent.forInsert(segmentId, profilingId).insert().getInt
	
	/**
	  * @param segmentId Id of targeted segment
	  * @param connection DB connection
	  * @return Last update event for that segment
	  */
	def lastSegmentUpdateEventFor(segmentId: Int)(implicit connection: Connection) =
		SegmentUpdateEvent.getMax("created", SegmentUpdateEvent.withSegmentId(segmentId).toCondition)
}
