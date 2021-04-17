package ch.mailchimp.controller

import utopia.flow.async.AsyncExtensions._
import utopia.flow.time.TimeExtensions._
import utopia.flow.generic.ValueConversions._
import utopia.flow.util.CollectionExtensions._
import ch.database.Entities
import ch.mailchimp.model.{APIConfiguration, ContactList}
import ch.mailchimp.util.{MD5, MailChimpSettings}
import ch.model.DataSet
import ch.util.Log
import utopia.access.http.Status.{NotFound, OK}
import utopia.access.http.StatusGroup
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.database.Connection

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  * Used for interacting with contact data in mail chimp API
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object ContactsAPI
{
	/**
	  * Pushes (posts or updates) contact data to MailChimp API
	  * @param list Targeted Contact list
	 *  @param dataForNewContact All data for the new contact, <b>including</b> that of associated linked entities
	  * @param connection DB connection
	  * @param exc Execution context
	  * @param configuration API request configuration
	  * @return A future of operation completion. None if no operation was performed (contact was missing an email)
	  */
	def push(list: ContactList, dataForNewContact: DataSet)(implicit connection: Connection, exc: ExecutionContext,
															configuration: APIConfiguration) =
	{
		// Finds out the email label(s) id first
		val emailLabels = Entities.labels.forTypeWithId(list.contentTypeId).emails
		
		// Makes sure contact has an email address
		emailLabels.findMap { label => dataForNewContact(label.id).flatMap { _._2.string } }.map { email =>
		
			// Checks whether specified email is already in the API, adds one if necessary
			val emailHash = MD5.hash(email)
			val mergeFields = makeMergeFieldsModel(list, dataForNewContact)
			ensureExistenceOf(email, emailHash, mergeFields, list).mapIfSuccess { alreadyExisted =>
				
				// If there already was a contact, updates its merge fields
				if (alreadyExisted)
				{
					API.patch(s"lists/${list.mailChimpListId}/members/$emailHash",
						Model(Vector("merge_fields" -> mergeFields)))
						.waitForResult(MailChimpSettings.requestTimeoutSeconds.seconds) match
					{
						case Success(response) =>
							if (response.status.group != StatusGroup.Success)
								Log.warning(s"Received non-OK status to contact patch. Response: $response")
						case Failure(error) =>
							Log(error, "Failed to update contact information")
					}
				}
			}
		}
	}
	
	private def ensureExistenceOf(email: String, emailHash: String, mergeFields: Model[Constant], list: ContactList)
								 (implicit exc: ExecutionContext, configuration: APIConfiguration) =
	{
		// First tries to read contact data
		API.get(s"lists/${list.mailChimpListId}/members/$emailHash").mapIfSuccess { getResponse =>
		
			getResponse.status match
			{
				// OK => No need to add a new contact
				case OK =>
					true
				case NotFound =>
					
					// Not found (contact doesn't exist yet) => Posts a new contact
					val contactModel = Model(Vector("email_address" -> email, "status" -> "subscribed",
						"merge_fields" -> mergeFields))
					
					API.post(s"lists/${list.mailChimpListId}/members/", contactModel)
						.waitForResult(MailChimpSettings.requestTimeoutSeconds.seconds) match
					{
						case Success(postResponse) =>
							if (postResponse.status.group != StatusGroup.Success)
								Log.warning(s"Received non-OK status to contact post (Contact: $contactModel). Response: $postResponse")
							false
							
						case Failure(error) =>
							Log(error, s"Failed to post contact data to MailChimp (Contact: $contactModel)")
							false
					}
					
				case _ =>
					Log.warning(s"Received an unexpected status to contact GET. Email: $email / $emailHash. Response: $getResponse")
					false
			}
		}
	}
	
	private def makeMergeFieldsModel(list: ContactList, data: DataSet)(implicit connection: Connection) =
	{
		// Fills merge fields with values
		val filledFields = list.mergeFields.map { field => field -> field(data) }
			.filter { _._2.isDefined }.map { case (field, value) => field.name -> value.get }
		
		// Forms a model from the fields
		Model(filledFields)
	}
}
