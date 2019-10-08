package ch.database.model

import ch.database.Tables
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.factory.StorableFactoryWithValidation

/**
 * Used for reading individual entity label group links from DB
 * @author Mikko Hilpinen
 * @since 21.8.2019, v1.1+
 */
object EntityLabelGroupContent extends StorableFactoryWithValidation[ch.model.EntityLabelGroupConnection]
{
	override protected def fromValidatedModel(model: Model[Constant]) = ch.model.EntityLabelGroupConnection(
		model("id").getInt, model("group").getInt, model("label").getInt, model("orderIndex").int)
	
	override def table = Tables.entityLabelGroupContent
}
