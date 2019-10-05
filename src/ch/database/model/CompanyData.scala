package ch.database.model

import ch.database.Tables
import utopia.flow.datastructure.immutable.Value

object CompanyData extends DataFactory[CompanyData]
{
	// IMPLEMENTED	---------------
	
	override def table = Tables.companyData
	
	override def readFactory = CompanyDataRead
	
	override def labelFactory = CompanyDataLabel
}

/**
  * Used for searching and updating company data
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
case class CompanyData(id: Option[Int] = None, readId: Option[Int] = None, labelId: Option[Int] = None,
					   value: Option[Value] = None) extends Data[CompanyData]
{
	// IMPLEMENTED	--------------
	
	override def factory = CompanyData
	
	override def makeCopy(id: Option[Int], readId: Option[Int], labelId: Option[Int], value: Option[Value]) =
		copy(id, readId, labelId, value)
}
