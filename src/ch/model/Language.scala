package ch.model

/**
 * Represents a language
 * @author Mikko Hilpinen
 * @since 25.10.2019, v3+
 * @param id This language's unique id
 * @param isoCode ISO standard code (two letters) for this language
 * @param localName Name of this language within this language
 * @param englishName Name of this language in English
 */
case class Language(id: Int, isoCode: String, localName: String, englishName: String)
