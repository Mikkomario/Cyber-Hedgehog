package ch.database.model

import java.time.Instant

import ch.database.Tables

object CompanyDataRead extends DataReadFactory[CompanyDataRead]
{
	// IMPLEMENTED	------------------
	
	override def table = Tables.companyDataRead
	
	override def targetPropertyName = "company"
	
	
	// OTHER	----------------------
	
	/**
	  * @param companyId Company id
	  * @return A read with specified company id
	  */
	def withCompanyId(companyId: Int) = withTargetId(companyId)
}

/**
  * Used for updating & searching for company data read events
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
case class CompanyDataRead(id: Option[Int] = None, sourceId: Option[Int] = None, targetId: Option[Int] = None,
						   dataOriginTime: Option[Instant] = None, created: Option[Instant] = None) extends DataRead
{
	override def factory = CompanyDataRead
	
	override def targetPropertyName = CompanyDataRead.targetPropertyName
}
