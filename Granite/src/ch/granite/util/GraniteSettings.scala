package ch.granite.util

import ch.util.Settings

/**
  * Contains granite-interface settings, which are read from a settings file
  * @author Mikko Hilpinen
  * @since 14.7.2019, v0.1+
  */
object GraniteSettings
{
	private def base = Settings.data("granite")
	
	/**
	  * @return Username used for authenticating Granite API requests
	  */
	def user = base("user").string
	
	/**
	  * @return Password used for authenticating Granite API requests
	  */
	def password = base("password").string
	
	/**
	  * @return Granite request domain (default = https://kirjaudu.tietosuojaturva.fi)
	  */
	def domain = base("domain").stringOr("https://kirjaudu.tietosuojaturva.fi")
	
	/**
	  * @return The API version used (default = v1)
	  */
	def apiVersion = base("apiVersion").stringOr("v1")
	
	/**
	  * @return How many seconds the client should wait for a response from Granite API
	  */
	def requestTimeoutSeconds = base("requestTimeoutSeconds").intOr(60)
}
