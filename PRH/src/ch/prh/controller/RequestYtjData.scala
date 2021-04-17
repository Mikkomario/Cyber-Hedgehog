package ch.prh.controller

import ch.util.Log
import utopia.access.http.Status.NotFound
import utopia.access.http.StatusGroup
import utopia.flow.async.AsyncExtensions._
import utopia.flow.time.TimeExtensions._
import utopia.disciple.apache.Gateway
import utopia.disciple.http.request.{Request, Timeout}
import utopia.flow.datastructure.template.{Model, Property}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Used for accessing PRH YTJ interface
 * @author Mikko Hilpinen
 * @since 20.11.2019, v3+
 */
object RequestYtjData
{
	private val baseUri = "https://avoindata.prh.fi/tr/v1/"
	private val timeout = 90.seconds
	
	private val gateway = new Gateway(maximumTimeout = Timeout(timeout))
	
	def apply(businessId: String)(implicit exc: ExecutionContext) =
	{
		val request = Request(baseUri + businessId)
		
		gateway.modelResponseFor(request).waitForResult(timeout) match
		{
			case Success(response) =>
				if (response.status.group == StatusGroup.Success)
				{
					// TODO: Continue
					// response.body
				}
				// Not found results are ignored, other failures are logged
				else if (response.status != NotFound)
					Log.warning(s"Server responded with status ${response.status} to query: $request. Response body: ${response.body}")
				
			case Failure(error) => Log(error, s"Couldn't get a response for $request")
		}
	}
	
	private def handleResult(resultModel: Model[Property]) =
	{
		// Reads some information from the result
		val companyName = resultModel("name").string
		val registrationDate = resultModel("registrationDate").localDate
		// TODO: Use mappings in DB to link fields to labels
	}
}
