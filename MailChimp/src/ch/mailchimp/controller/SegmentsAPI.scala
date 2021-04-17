package ch.mailchimp.controller

import utopia.flow.generic.ValueConversions._
import ch.mailchimp.model.{APIConfiguration, ContactList, ContactSegment}
import ch.util.Log
import utopia.access.http.StatusGroup
import utopia.flow.datastructure.immutable.Model

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  * Used for interacting with MailChimp API segments
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object SegmentsAPI
{
	/**
	  * Updates segment members on MailChimp API
	  * @param list Targeted list
	  * @param segment Targeted segment
	  * @param newEmails Emails to add to segment
	  * @param removedEmails Emails to remove from segment
	  * @param exc Execution context
	  * @param configuration API interface configuration
	  * @return A future of operation results
	  */
	def updateSegmentMembers(list: ContactList, segment: ContactSegment, newEmails: Vector[String],
							 removedEmails: Vector[String])
							(implicit exc: ExecutionContext, configuration: APIConfiguration) =
	{
		// Makes the post
		API.post(s"lists/${list.mailChimpListId}/segments/${segment.mailChimpSegmentId}",
			Model(Vector("members_to_add" -> newEmails, "members_to_remove" -> removedEmails)))
			.map {
				case Success(response) =>
					if (response.status.group != StatusGroup.Success)
						Log.warning(s"MailChimp API returned a non-OK response for segment update. Response: $response")
				case Failure(error) => Log(error, "Failed to update mail chimp segment members")
			}
	}
}
