package ch.database

import utopia.flow.generic.ValueConversions._
import ch.model.scoring
import utopia.flow.datastructure.immutable.Value
import utopia.vault.database.Connection
import utopia.vault.model.immutable.access.{SingleAccessWithIds, SingleIdAccess}

object AlgorithmIdAccess extends SingleIdAccess[Int]
{
	// COMPUTED	-------------------------
	
	/**
	 * @param connection DB connection (implicit)
	 * @return The id of the latest algorithm version
	 */
	def latest(implicit connection: Connection) = max("created")
	
	
	// IMPLEMENTED	---------------------
	
	override def table = Tables.riskAlgorithm
	override protected def valueToId(value: Value) = value.getInt
	
	
	// OTHER	-------------------------
	
	def ofLatestVersionForTypeWithId(typeId: Int)(implicit connection: Connection) =
		max(model.Algorithm.withTargetTypeId(typeId).toCondition, "created")
}

/**
 * Used for accessing individual algorithm versions
 * @author Mikko Hilpinen
 * @since 25.8.2019, v1.1+
 */
object Algorithm extends SingleAccessWithIds[Int, scoring.Algorithm, AlgorithmIdAccess.type]
{
	override def id = AlgorithmIdAccess
	
	override protected def idValue(id: Int) = id
	
	override def factory = model.Algorithm
}
