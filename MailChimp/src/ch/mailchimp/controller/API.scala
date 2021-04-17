package ch.mailchimp.controller

import ch.mailchimp.model.APIConfiguration
import utopia.access.http.Method.{Get, Patch, Post}
import utopia.access.http.{Headers, Method}
import utopia.disciple.apache.Gateway
import utopia.disciple.http.request.{Request, StringBody}
import utopia.disciple.http.response.BufferedResponse
import utopia.flow.datastructure.immutable.{Constant, Model}

import scala.concurrent.ExecutionContext

/**
  * Used for interacting with MailChimp API (v3.0)
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object API
{
	private val gateway = new Gateway(allowBodyParameters = false)
	
	/**
	  * Performs a request to mailChimp API
	  * @param method Method used in request
	  * @param uriEnd End portion of targeted uri (portion after ".../api.mailchimp.com/3.0/")
	  * @param bodyModel Body sent along with the request (default = empty body = no body)
	  * @param params Query parameters sent with the request (default = no parameters)
	  * @param exc Execution context
	  * @param configuration Api request configuration
	  * @return Future of received response string. May contain a failure and may fail itself.
	  */
	def request(method: Method, uriEnd: String, bodyModel: Model[Constant] = Model.empty,
				params: Model[Constant] = Model.empty)(implicit exc: ExecutionContext, configuration: APIConfiguration) =
	{
		// Forms the request first
		val uri = s"https://${configuration.dataCenter}.api.mailchimp.com/3.0/$uriEnd"
		val headers = Headers().withBasicAuthorization("any", configuration.apiKey)
		val body = if (bodyModel.isEmpty) None else Some(StringBody.json(bodyModel.toJson))
		val request = Request(uri, method, params, headers, body)
		
		// println(s"Sending request: $request")
		
		// Performs the request and handles the response asynchronously
		gateway.stringResponseFor(request)
	}
	
	/**
	  * Performs a GET request to MailChimp API
	  * @param uriEnd End portion of targeted uri (portion after ".../api.mailchimp.com/3.0/")
	  * @param params Query parameters sent with the request (default = no parameters)
	  * @param exc    Execution context
	  * @param configuration Api request configuration
	  * @return Future of received response string. May contain a failure and may fail itself.
	  */
	def get(uriEnd: String, params: Model[Constant] = Model.empty)
		   (implicit exc: ExecutionContext, configuration: APIConfiguration) =
		request(Get, uriEnd, params = params)
	
	/**
	  * Performs a POST request to MailChimp API
	  * @param uriEnd End portion of targeted uri (portion after ".../api.mailchimp.com/3.0/")
	  * @param body Body sent along with the request (default = empty body = no body)
	  * @param exc Execution context
	  * @param configuration Api request configuration
	  * @return Future of received response string. May contain a failure and may fail itself.
	  */
	def post(uriEnd: String, body: Model[Constant])
			(implicit exc: ExecutionContext, configuration: APIConfiguration) =
		request(Post, uriEnd, bodyModel = body)
	
	/**
	  * Performs a PATCH request to MailChimp API
	  * @param uriEnd End portion of targeted uri (portion after ".../api.mailchimp.com/3.0/")
	  * @param body Body sent along with the request (default = empty body = no body)
	  * @param exc Execution context
	  * @param configuration Api request configuration
	  * @return Future of received response string. May contain a failure and may fail itself.
	  */
	def patch(uriEnd: String, body: Model[Constant])
			(implicit exc: ExecutionContext, configuration: APIConfiguration) =
		request(Patch, uriEnd, bodyModel = body)
}

private class RequestFailedException(request: Request, response: BufferedResponse[_]) extends
	RuntimeException(s"MailChimp API Request Failed. Request: $request. Reponse: ${response.status}, ${response.body}")