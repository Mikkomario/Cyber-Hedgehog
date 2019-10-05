package ch.database.model

import utopia.flow.generic.ValueConversions._
import ch.database.Tables
import utopia.vault.model.immutable.Storable

object CompanySegmentConnection
{
	/**
	  * Creates a new model ready to be inserted to DB
	  * @param profilingId Id of associated profiling event
	  * @param companyId Id of connected company
	  */
	def forInsert(profilingId: Int, companyId: Int) = CompanySegmentConnection(None, Some(profilingId), Some(companyId))
	
	/**
	  * @param profilingId Id of associated profiling event
	  * @return A model with only profilingId set
	  */
	def withProfilingId(profilingId: Int) = CompanySegmentConnection(profilingId = Some(profilingId))
}

/**
  * Used for searching and updating company segment connections (per profile) in DB
  * @author Mikko Hilpinen
  * @since 19.7.2019, v0.1+
  */
case class CompanySegmentConnection(id: Option[Int] = None, profilingId: Option[Int] = None, companyId: Option[Int] = None)
	extends Storable
{
	override def table = Tables.companySegmentConnection
	
	override def valueProperties = Vector("id" -> id, "profiling" -> profilingId, "company" -> companyId)
}
