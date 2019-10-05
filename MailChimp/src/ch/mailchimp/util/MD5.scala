package ch.mailchimp.util

import java.security.MessageDigest
import java.math.BigInteger

/**
  * Used for hashing string data to MD5 form
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object MD5
{
	/**
	  * Hashes a string to MD5
	  * @param s A string to hash
	  * @return Hashed string
	  */
	// Original code at: https://alvinalexander.com/source-code/scala-method-create-md5-hash-of-string
	def hash(s: String): String =
	{
		val md = MessageDigest.getInstance("MD5")
		val digest = md.digest(s.getBytes)
		
		val bigInt = new BigInteger(1,digest)
		val hashedString = bigInt.toString(16)
		
		hashedString
	}
}
