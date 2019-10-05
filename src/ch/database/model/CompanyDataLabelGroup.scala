package ch.database.model

import ch.database.Tables
import ch.model
import ch.model.CompanyDataLabelGroupConnection
import utopia.flow.datastructure.immutable.{Constant, Model, Value}
import utopia.vault.model.immutable.factory.MultiLinkedStorableFactory
import utopia.flow.util.CollectionExtensions._

/**
 * Used for reading company data label groups / linkings from DB
 * @author Mikko Hilpinen
 * @since 21.8.2019, v1.1+
 */
object CompanyDataLabelGroup extends MultiLinkedStorableFactory[model.CompanyDataLabelGroup,
	model.CompanyDataLabelGroupConnection]
{
	override def table = Tables.companyDataLabelGroup
	
	override def childFactory = CompanyDataLabelGroupContent
	
	override def apply(id: Value, model: Model[Constant], children: Seq[CompanyDataLabelGroupConnection]) =
	{
		table.requirementDeclaration.validate(model).toTry.map { valid =>
			
			// Orders the connection based on order index or row index
			val (noOrdering, ordering) = children.divideBy { _.orderIndex.isDefined }
			val labelIds = (ordering.sortBy { _.orderIndex.get } ++ noOrdering.sortBy { _.id }).map { _.labelId }
			
			ch.model.CompanyDataLabelGroup(id.getInt, labelIds)
		}
	}
}
