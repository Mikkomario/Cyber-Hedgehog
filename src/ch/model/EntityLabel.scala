package ch.model

/**
  * Represents a field for company- or other kind of data data
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  * @param id This label's unique identifier
 *  @param targetEntityTypeId Id of the entity type this label describes
 *  @param currentConfiguration The current configuration of this label (optional)
  */
case class EntityLabel(id: Int, targetEntityTypeId: Int, currentConfiguration: Option[EntityLabelConfiguration])
{
	/**
	 * @return The data type of this label's contents
	 */
	def dataType = currentConfiguration.map { _.dataType }.getOrElse { DataType.StringType }
	
	/**
	 * @return Whether this label should be considered an identifier
	 */
	def isIdentifier = currentConfiguration.exists { _.isIdentifier }
	
	/**
	 * @return Whether this label represents an email
	 */
	def isEmail = currentConfiguration.exists { _.isEmail }
}
