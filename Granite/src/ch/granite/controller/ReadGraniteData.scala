package ch.granite.controller

import java.time.Instant

import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.TimeExtensions._
import utopia.flow.async.AsyncExtensions._
import ch.database.{ConnectionPool, DataRead}
import ch.granite.database.{Service, Services}
import ch.granite.model.{Granite, LabelMapping, LinkTypeMapping}
import ch.granite.util.GraniteSettings
import ch.model.exception.InvalidSettingsException
import ch.util.Log
import utopia.access.http.{Headers, StatusGroup}
import utopia.disciple.apache.Gateway
import utopia.disciple.http.{BufferedResponse, Request}
import utopia.flow.parse.JSONReader
import utopia.vault.database.Connection

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Reads data from Granite servers and stores it to the database
  * @author Mikko Hilpinen
  * @since 14.7.2019, v0.1+
  */
object ReadGraniteData
{
	// OTHER	--------------------------
	
	/**
	  * Reads and parses new responses from Granite API. The read is done asynchronously
	  * @param executionContext Asynchronous execution context
	  * @return Future of how many new responses were parsed. May contain a failure.
	  */
	def asynchronously()(implicit executionContext: ExecutionContext) = Future { ConnectionPool.tryWith {
		implicit connection => synchronously() }.flatten }
	
	/**
	  * Reads and parses new responses from Granite API. The read is done synchronously, which means that this function
	  * will block for quite a while.
	  * @param executionContext Asynchronous execution context
	  * @return How many new responses were parsed. May contain a failure.
	  */
	def synchronously()(implicit executionContext: ExecutionContext, connection: Connection) =
	{
		// Makes sure user name and password are available
		if (GraniteSettings.user.isEmpty)
			Failure(new InvalidSettingsException("No user provided in settings"))
		else if (GraniteSettings.password.isEmpty)
			Failure(new InvalidSettingsException("No password provided in settings"))
		else
		{
			val user = GraniteSettings.user.get
			val password = GraniteSettings.password.get
			
			// Finds the latest data read time
			val lastReadTime = DataRead.fromSourceWithId(Granite.id).latest.map { _.readTime }
			
			// Finds targeted services
			val services = Services.all
			if (services.isEmpty)
			{
				Log.warning("No service data could be found from the database")
				Success(0)
			}
			else
			{
				var parsedResponseCount = 0
				
				// Requests data from each service. Starts by searching for new responses
				val serviceHandleFailure = services.findMap { service =>
					
					newResponseIdsForService(service.graniteId, lastReadTime, user, password) match
					{
						case Success(responseIds) =>
							
							if (responseIds.isEmpty)
								None
							else
							{
								// If there were new responses, reads field & mapping data and starts parsing the responses.
								// Response handling may fail, however
								val serviceAccess = Service(service.id)
								val fieldMappings = serviceAccess.fields.labelMappings.get
								val optionMappings = serviceAccess.options.labelMappings.get
								val labelMappings = fieldMappings ++ optionMappings
								val linkMappings = serviceAccess.options.linkTypeMappings.get
								
								responseIds.findMap { responseId =>
									
									readResponse(responseId, user, password, labelMappings, linkMappings) match
									{
										case Success(_) =>
											parsedResponseCount += 1
											None
										case Failure(error) => Some(error)
									}
								}
							}
						
						case Failure(error) => Some(error)
					}
				}
				
				serviceHandleFailure.map { Failure(_) } getOrElse Success(parsedResponseCount)
			}
		}
	}
	
	private def baseUri = s"${GraniteSettings.domain}/api/${GraniteSettings.apiVersion}"
	
	private def listResponsesForServiceUri(serviceId: Int) =
		s"$baseUri/services/$serviceId/instances/contents/formStructures/formResponses?language=en&view=basic"
	
	private def getResponseUri(responseId: Int) =
		s"$baseUri/services/instances/contents/formStructures/formResponses/$responseId?embed=sections&language=en&view=basic"
	
	private def checkResponse[R](request: Request, response: BufferedResponse[Try[String]], parse: String => Try[R]) =
	{
		if (response.status.group != StatusGroup.Success)
			Failure(new RequestFailedException(request, response, "Granite request failed"))
		else
			response.body.flatMap(parse)
	}
	
	private def newResponseIdsForService(serviceId: Int, lastReadTime: Option[Instant], user: String, password: String)
						   (implicit executionContext: ExecutionContext, connection: Connection) =
	{
		// First reads all response meta data and checks if there are some that were recently created
		val request = new Request(listResponsesForServiceUri(serviceId), headers = Headers().withBasicAuthorization(user, password))
		val responseReceive = Gateway.getStringResponse(request)
		
		responseReceive.waitFor(GraniteSettings.requestTimeoutSeconds.seconds).flatMap { response =>
			
			checkResponse(request, response, JSONReader.apply).map { results =>
				
				// Result should be an array, finds ids of responses that have been created after last read
				results.getVector.flatMap { result =>
				
					val model = result.getModel
					val id = model("id").int
					val created = model("created").instant
					
					if (id.isEmpty || created.isEmpty)
						Log.warning(s"Unexpected result when reading results from granite. Expected model to have 'id' and 'created'. Instead received $model")
					
					if (id.isDefined && created.isDefined && lastReadTime.forall { last => created.get > last  })
						Some(id.get -> created.get)
					else
						None
					
					// Sorts results based on creation time
				}.sortBy { _._2 }.map { _._1 }
			}
		}
	}
	
	private def readResponse(responseId: Int, user: String, password: String, labelMappings: Traversable[LabelMapping],
							 linkMappings: Traversable[LinkTypeMapping])
							(implicit executionContext: ExecutionContext, connection: Connection) =
	{
		// Requests response data from server
		val request = new Request(getResponseUri(responseId), headers = Headers().withBasicAuthorization(user, password))
		val responseReceive = Gateway.getStringResponse(request)
		
		responseReceive.waitFor(GraniteSettings.requestTimeoutSeconds.seconds).flatMap { response =>
			
			// Expects a json model response
			checkResponse(request, response, JSONReader(_).map { _.getModel }).map { model =>
			
				// Parses model into a result and handles it
				ResultHandler(QueryParser(model), labelMappings, linkMappings)
			}
		}
	}
}

private class RequestFailedException(request: Request, response: BufferedResponse[_], additionalMessage: String) extends
	RuntimeException(s"$additionalMessage. Request: $request. Reponse: ${response.status}, ${response.body}")
