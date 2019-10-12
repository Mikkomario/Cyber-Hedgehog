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
 *  @param labelId Id Of associated entity label
  */
case class MergeField(id: Int, listId: Int, mergeId: Int, name: String, labelId: Int)
{
	override def toString = s"$id ($mergeId, $name)"
	
	/**
	  * Finds value for this merge field from provided contact / company data
	 *  @param data All entity data available. May contain data from multiple linked entities
	  * @return Value for this field. None if no data could be found.
	  */
	def apply(data: DataSet) = data(labelId).map { _._2 }
}