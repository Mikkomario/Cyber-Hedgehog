package ch.database

import ch.database.model.{CompanyDataLabel, CompanyDataRead}
import utopia.vault.database.Connection

/**
  * Used for interacting with company DB data
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
@deprecated("Please use Entity instead", "v2")
object Company extends DataInterface[model.CompanyData, CompanyDataRead, CompanyDataLabel]
{
	// IMPLEMENTED	--------------------
	
	override def dataFactory = model.CompanyData
	
	override def readFactory = CompanyDataRead
	
	override def labelFactory = CompanyDataLabel
	
	
	// OTHER	------------------------
	
	/**
	  * @param connection DB connection
	  * @return Ids for all recorded companies
	  */
	@deprecated("Replaced with Entities.ids.all", "v2")
	def allIds(implicit connection: Connection) = Tables.company.allIndices.flatMap { _.int }
	
	/**
	  * Inserts a new company to the DB
	  * @param sourceId Origin source id
	  * @param connection DB connection
	  * @return Newly added company id
	  */
	@deprecated("Replaced with Entity.insert(...)", "v2")
	def insert(sourceId: Int)(implicit connection: Connection) = model.Company.forInsert(sourceId).insert().getInt
}