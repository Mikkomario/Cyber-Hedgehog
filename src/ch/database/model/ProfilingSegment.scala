package ch.database.model

import utopia.flow.generic.ValueConversions._
import ch.database.Tables
import ch.model.profiling
import utopia.flow.datastructure.immutable.{Constant, Model}
import utopia.vault.model.immutable.StorableWithFactory
import utopia.vault.model.immutable.factory.StorableFactoryWithValidation

object ProfilingSegment extends StorableFactoryWithValidation[profiling.ProfilingSegment]
{
	override protected def fromValidatedModel(model: Model[Constant]) = profiling.ProfilingSegment(model("id").getInt)
	
	override def table = Tables.segment
}

/**
  * Used for interacting with segment DB data
  * @author Mikko Hilpinen
  * @since 30.7.2019, v1.1+
  */
case class ProfilingSegment(id: Option[Int] = None) extends StorableWithFactory[profiling.ProfilingSegment]
{
	override def factory = ProfilingSegment
	
	override def valueProperties = Vector("id" -> id)
}
