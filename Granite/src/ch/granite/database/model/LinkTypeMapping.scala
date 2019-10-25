package ch.granite.database.model

import ch.granite.database.Tables
import utopia.flow.generic.ValueConversions._
import ch.granite.model
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.immutable.factory.LinkedStorableFactory

object LinkTypeMapping extends LinkedStorableFactory[model.LinkTypeMapping, model.SelectOption]
{
	// IMPLEMENTED	------------------------
	
	override def table = Tables.linkTypeMapping
	
	override def childFactory = SelectOption
	
	override def apply(model: Model[Constant], child: ch.granite.model.SelectOption) =
		table.requirementDeclaration.validate(model).toTry.map { valid => ch.granite.model.LinkTypeMapping(
			valid("id").getInt, child, valid("originType").getInt, valid("targetType").getInt, valid("linkType").getInt)
	}
	
	
	// OTHER	----------------------------
	
	/**
	 * @param typeId Id of link origin entity type
	 * @return Model with only origin type id set
	 */
	def withOriginTypeId(typeId: Int) = LinkTypeMapping(originTypeId = Some(typeId))
}

/**
 * Used for interacting with granite link type mapping DB data
 * @author Mikko Hilpinen
 * @since 6.10.2019, v2+
 */
case class LinkTypeMapping(id: Option[Int] = None, optionId: Option[Int] = None, originTypeId: Option[Int] = None,
						   targetTypeId: Option[Int] = None, linkTypeId: Option[Int] = None)
	extends StorableWithFactory[model.LinkTypeMapping]
{
	override def factory = LinkTypeMapping
	
	override def valueProperties = Vector("id" -> id, "option" -> optionId,
		"originType" -> originTypeId, "targetType" -> targetTypeId, "linkType" -> linkTypeId)
}
