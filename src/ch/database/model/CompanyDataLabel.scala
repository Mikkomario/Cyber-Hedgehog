package ch.database.model

import ch.database.Tables
import ch.model.DataType

object CompanyDataLabel extends DataLabelFactory[CompanyDataLabel]
{
	// IMPLEMENTED	---------------------
	
	override def table = Tables.companyDataLabel
}

/**
  * Used for searching and updating company data label DB data
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
case class CompanyDataLabel(id: Option[Int] = None, dataType: Option[DataType] = None,
							isIdentifier: Option[Boolean] = None, isEmail: Option[Boolean] = None) extends DataLabel
{
	override def factory = CompanyDataLabel
}
