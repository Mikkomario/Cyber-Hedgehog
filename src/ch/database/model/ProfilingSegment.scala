package ch.database.model

import utopia.flow.generic.ValueConversions._
import ch.database.Tables
import ch.model.profiling
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.nosql.factory.FromValidatedRowModelFactory

object ProfilingSegment extends FromValidatedRowModelFactory[profiling.ProfilingSegment]
{
	// IMPLEMENTED	--------------------
	
	override protected def fromValidatedModel(model: Model[Constant]) = profiling.ProfilingSegment(model("id").getInt,
		model("contentType").getInt)
	
	override def table = Tables.segment
	
	
	// OTHER	------------------------
	
	/**
	 * @param contentTypeId Id of targeted entity type
	 * @return A model with content type id set
	 */
	def withContentTypeId(contentTypeId: Int) = ProfilingSegment(contentTypeId = Some(contentTypeId))
}

/**
  * Used for interacting with segment DB data
  * @author Mikko Hilpinen
  * @since 30.7.2019, v1.1+
  */
case class ProfilingSegment(id: Option[Int] = None, contentTypeId: Option[Int] = None)
	extends StorableWithFactory[profiling.ProfilingSegment]
{
	override def factory = ProfilingSegment
	
	override def valueProperties = Vector("id" -> id, "contentType" -> contentTypeId)
}
