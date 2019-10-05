package ch.model

/**
 * Used for combining multiple company data labels into a single group
 * @author Mikko Hilpinen
 * @since 21.8.2019, v1.1+
 * @param id Unique id for this group
 * @param labelIds Ids of this group's labels in order
 */
case class CompanyDataLabelGroup(id: Int, labelIds: Seq[Int])
