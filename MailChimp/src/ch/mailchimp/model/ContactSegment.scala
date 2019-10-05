package ch.mailchimp.model

/**
  * Represents a sub-group (segment) in a contact list
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  * @param id This segment's unique id (in DB)
  * @param listId Id of the list associated with this segment
  * @param profilingSegmentId Id of linked profiling segment
  * @param mailChimpSegmentId Id of this segment in mail chimp
  */
case class ContactSegment(id: Int, listId: Int, profilingSegmentId: Int, mailChimpSegmentId: String)
{
	override def toString = s"$id ($mailChimpSegmentId) (linked to profiling $profilingSegmentId)"
}
