package ch.database.model

import utopia.flow.generic.ValueConversions._
import ch.database.Tables
import utopia.vault.model.immutable.Storable

object Company
{
	/**
	  * Creates a company model ready to inserted to DB
	  * @param sourceId Origin source identifier
	  * @return A company model ready to be inserted
	  */
	def forInsert(sourceId: Int) = Company(sourceId = Some(sourceId))
	
	/**
	  * @param id Company id
	  * @return Model with id set
	  */
	def withId(id: Int) = Company(id = Some(id))
}

/**
  * Used for searching and updating company data
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
case class Company(id: Option[Int] = None, sourceId: Option[Int] = None) extends Storable
{
	override def table = Tables.company
	
	override def valueProperties = Vector("id" -> id, "source" -> sourceId)
}
