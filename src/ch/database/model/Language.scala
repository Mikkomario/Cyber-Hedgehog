package ch.database.model

import utopia.flow.generic.ValueConversions._
import ch.database.Tables
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.factory.FromValidatedRowModelFactory

object Language extends FromValidatedRowModelFactory[ch.model.Language]
{
	// IMPLEMENTED	---------------------
	
	override protected def fromValidatedModel(model: Model[Constant]) = ch.model.Language(model("id").getInt,
		model("isoCode").getString, model("localName").getString, model("englishName").getString)
	
	override def table = Tables.language
	
	
	// OTHER	-------------------------
	
	/**
	 * @param isoCode ISO standard code for language
	 * @return A model with only code set
	 */
	def withCode(isoCode: String) = Language(isoCode = Some(isoCode))
}

/**
 * Used for interacting with language DB data
 * @author Mikko Hilpinen
 * @since 25.10.2019, v3+
 */
case class Language(id: Option[Int] = None, isoCode: Option[String] = None, localName: Option[String] = None,
					englishName: Option[String] = None) extends StorableWithFactory[ch.model.Language]
{
	// IMPLEMENTED	---------------------
	
	override def factory = Language
	
	override def valueProperties = Vector("id" -> id, "isoCode" -> isoCode, "localName" -> localName,
		"englishName" -> englishName)
}
