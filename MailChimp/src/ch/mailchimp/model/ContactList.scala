package ch.mailchimp.model

/**
  * Represents a contact list in mailChimp
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  * @param id This list's unique id
  * @param mailChimpListId This list's id in mailChimp platform
  * @param segments Segments under this list
  * @param mergeFields Merge fields used in this list
  */
case class ContactList(id: Int, mailChimpListId: String, segments: Vector[ContactSegment], mergeFields: Vector[MergeField])
{
	override def toString = s"$id ($mailChimpListId). Segments: [${segments.mkString(", ")}]. Merge fields: [${mergeFields.mkString(", ")}]]"
}
