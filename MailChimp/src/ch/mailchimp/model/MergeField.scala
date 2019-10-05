package ch.mailchimp.model

import ch.model.DataSet

/**
  * Represents a merge field in mailChimp
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  * @param id This field's unique id
  * @param listId Id of this field's list context
  * @param mergeId This field's 'merge id' in mailChimp platform
  * @param name This field's name in mailChimp
  * @param contactLabelId Id of associated contact label (None if not associated with a contact label)
  * @param companyLabelId Id of associated company label (None if not associated with a company label)
  */
case class MergeField(id: Int, listId: Int, mergeId: Int, name: String,
					  contactLabelId: Option[Int], companyLabelId: Option[Int])
{
	override def toString = s"$id ($mergeId, $name)"
	
	/**
	  * Finds value for this merge field from provided contact / company data
	  * @param contactData Contact data available
	  * @param companyData Company data available
	  * @return Label value pair for this field. None if no data could be found.
	  */
	def apply(contactData: DataSet, companyData: Option[DataSet]) = contactLabelId.flatMap { contactData(_) }
		.orElse { companyLabelId.flatMap { labelId => companyData.flatMap { _(labelId) } } }.map { _._2 }
}