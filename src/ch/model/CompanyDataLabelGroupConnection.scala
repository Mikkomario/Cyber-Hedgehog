package ch.model

/**
 * Represents a link between a company data label and a label group
 * @author Mikko Hilpinen
 * @since 21.8.2019, v1.1+
 * @param id This link's unique id
 * @param groupId Id of linked group
 * @param labelId Id of linked company data label
 * @param orderIndex Ordering index for this link, if specified
 */
case class CompanyDataLabelGroupConnection(id: Int, groupId: Int, labelId: Int, orderIndex: Option[Int])
