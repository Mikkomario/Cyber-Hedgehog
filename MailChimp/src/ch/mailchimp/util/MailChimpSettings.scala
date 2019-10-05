package ch.mailchimp.util

import ch.util.Settings

/**
  * Used for reading mail chimp -specific settings from the settings file
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object MailChimpSettings
{
	private def base = Settings.data("mailChimp")
	
	/**
	  * @return MailChimp API-key used for authenticating API requests. None if not specified.
	  */
	def apiKey = base("apiKey").string
	
	/**
	  * @return Data center portion of API request uris. May be parsed from last portion of API-key if omitted in
	  *         settings. None if no API-key provided or if couldn't be parsed from the API-key.
	  */
	def dataCenter = base("dataCenter").string.orElse
	{
		// Parses DataCenter from the last portion of API-key
		apiKey.flatMap { key =>
		
			val lastDash = key.lastIndexOf('-')
			if (lastDash < 0 || lastDash >= key.length - 1)
				None
			else
				Some(key.substring(lastDash + 1))
		}
	}
	
	/**
	  * @return Timeout (seconds) used when making requests to MailChimp API. Default = 60.
	  */
	def requestTimeoutSeconds = base("requestTimeoutSeconds").intOr(60)
}
