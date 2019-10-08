package ch.granite.database.model

import ch.database.model.CompanyDataLabel
import ch.granite.database.Tables

@deprecated("Replaced with FieldLabelMapping and OptionLabelMapping", "v2")
object CompanyLabelMapping extends LabelMappingFactory[CompanyLabelMapping]
{
	override def table = Tables.companyLabelMapping
	
	override protected def labelFactory = CompanyDataLabel
	
	override def withFieldId(fieldId: Int) = CompanyLabelMapping(fieldId = Some(fieldId))
	
	override def withOptionId(optionId: Int) = CompanyLabelMapping(optionId = Some(optionId))
}

/**
  * Used for searching and updating company label mapping data
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
@deprecated("Replaced with FieldLabelMapping and OptionLabelMapping", "v2")
case class CompanyLabelMapping(id: Option[Int] = None, fieldId: Option[Int] = None, optionId: Option[Int] = None,
							   labelId: Option[Int] = None) extends LabelMapping
{
	override def factory = CompanyLabelMapping
}
