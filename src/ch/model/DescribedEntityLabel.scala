package ch.model

import utopia.flow.util.CollectionExtensions._

/**
 * This version of entity label also includes human readable descriptions
 * @author Mikko Hilpinen
 * @since 30.10.2019, v3+
 * @param label Described label
 * @param descriptions Label descriptions, with most important before the least important
 */
case class DescribedEntityLabel(label: EntityLabel, descriptions: Vector[EntityLabelDescription])
{
	// COMPUTED	---------------------
	
	/**
	 * @return Data type of this label
	 */
	def dataType = label.dataType
	/**
	 * @return Whether this label represents an email
	 */
	def isEmail = label.isEmail
	/**
	 * @return Whether this label should be considered an identifier
	 */
	def isIdentifier = label.isIdentifier
	/**
	 * @return Name of this label (from primary description). None if this label is not properly described
	 */
	def name = descriptions.headOption.map { _.name }
	/**
	 * @return Description of this label. None if this label is not properly described
	 */
	def description = descriptions.findMap { _.description }
}
