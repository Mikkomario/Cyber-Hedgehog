package ch.database

import utopia.flow.generic.ValueConversions._
import ch.model.scoring
import utopia.vault.database.Connection
import utopia.vault.nosql.access.{SingleIntIdAccess, SingleModelAccessById}

object AlgorithmIdAccess extends SingleIntIdAccess
{
	// COMPUTED	-------------------------
	
	/**
	 * @param connection DB connection (implicit)
	 * @return The id of the latest algorithm version
	 */
	def latest(implicit connection: Connection) = maxBy("created")
	
	
	// IMPLEMENTED	---------------------
	
	override def target = table
	
	override def globalCondition = None
	
	override def table = Tables.riskAlgorithm
	
	
	// OTHER	-------------------------
	
	def ofLatestVersionForTypeWithId(typeId: Int)(implicit connection: Connection) =
		maxBy("created", Some(model.Algorithm.withTargetTypeId(typeId).toCondition))
}

/**
 * Used for accessing individual algorithm versions
 * @author Mikko Hilpinen
 * @since 25.8.2019, v1.1+
 */
object Algorithm extends SingleModelAccessById[scoring.Algorithm, Int]
{
	def id = AlgorithmIdAccess
	
	override def idToValue(id: Int) = id
	
	override def factory = model.Algorithm
}
