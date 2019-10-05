package ch.mailchimp.model

import ch.mailchimp.util.MailChimpSettings
import ch.model.exception.InvalidSettingsException

import scala.util.{Failure, Success}

object APIConfiguration
{
	/**
	  * @return API Configuration read from settings
	  */
	def fromSettings =
	{
		val key = MailChimpSettings.apiKey
		val dataCenter = MailChimpSettings.dataCenter
		
		if (key.isEmpty)
			Failure(new InvalidSettingsException("MailChimp/apiKey required in settings for MailChimp API configurations"))
		else if (dataCenter.isEmpty)
			Failure(new InvalidSettingsException("MailChimp/apiKey or MailChimp/dataCenter is malformed and can't be used"))
		else
			Success(APIConfiguration(key.get, dataCenter.get))
	}
}

/**
  * Contains configurations required when using MailChimp API
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  * @param apiKey API key used to authenticate requests
  * @param dataCenter DataCenter part used in request uris
  */
case class APIConfiguration(apiKey: String, dataCenter: String)
