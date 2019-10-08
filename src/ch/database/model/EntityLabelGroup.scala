package ch.database.model

import ch.database.Tables
import ch.model
import ch.model.EntityLabelGroupConnection
import utopia.flow.datastructure.immutable.{Constant, Model, Value}
import utopia.vault.model.immutable.factory.MultiLinkedStorableFactory
import utopia.flow.util.CollectionExtensions._

/**
 * Used for reading company data label groups / linkings from DB
 * @author Mikko Hilpinen
 * @since 21.8.2019, v1.1+
 */
object EntityLabelGroup extends MultiLinkedStorableFactory[model.EntityLabelGroup,
	model.EntityLabelGroupConnection]
{
	override def table = Tables.entityLabelGroup
	
	override def childFactory = EntityLabelGroupContent
	
	override def apply(id: Value, model: Model[Constant], children: Seq[EntityLabelGroupConnection]) =
	{
		table.requirementDeclaration.validate(model).toTry.map { _ =>
			
			// Orders the connection based on order index or row index
			val (noOrdering, ordering) = children.divideBy { _.orderIndex.isDefined }
			val labelIds = (ordering.sortBy { _.orderIndex.get } ++ noOrdering.sortBy { _.id }).map { _.labelId }
			
			ch.model.EntityLabelGroup(id.getInt, labelIds)
		}
	}
}
