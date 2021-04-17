package ch.database.model

import ch.database.Tables
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.flow.generic.ValueConversions._
import utopia.vault.nosql.factory.FromValidatedRowModelFactory

object Entity extends FromValidatedRowModelFactory[ch.model.Entity]
{
	// IMPLEMENTED	------------------
	
	override protected def fromValidatedModel(model: Model[Constant]) = ch.model.Entity(model("id").getInt,
		model("type").getInt, model("source").getInt)
	
	def table = Tables.entity
	
	
	// OTHER	--------------------
	
	/**
	 * @param id Entity id
	 * @return A model with just id
	 */
	def withId(id: Int) = Entity(id = Some(id))
	
	/**
	 * @param typeId Entity type id
	 * @return A model with only type id
	 */
	def withTypeId(typeId: Int) = Entity(typeId = Some(typeId))
	
	/**
	 * @param typeId Id of associated entity type
	 * @param sourceId Id of data source
	 * @return A model ready to be inserted to DB
	 */
	def forInsert(typeId: Int, sourceId: Int) = Entity(None, Some(typeId), Some(sourceId))
}

/**
 * Used for interacting with entity DB data
 * @author Mikko Hilpinen
 * @since 5.10.2019, v2+
 */
case class Entity(id: Option[Int] = None, typeId: Option[Int] = None, sourceId: Option[Int] = None)
	extends StorableWithFactory[ch.model.Entity]
{
	override def factory = Entity
	
	override def valueProperties = Vector("id" -> id, "type" -> typeId, "source" -> sourceId)
}
