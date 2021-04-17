package ch.controller

import java.time.Instant

import utopia.flow.util.CollectionExtensions._
import utopia.flow.time.TimeExtensions._
import ch.database.{Entities, Entity, Profiling}
import ch.model.{DataRead, DataSet}
import ch.model.profiling.condition.PartialSegmentFilter
import ch.model.profiling.ProfilingEvent
import utopia.vault.database.Connection
import ch.database

/**
  * Searches through entity data and assigns entities to different segments based on a filter logic
  * @author Mikko Hilpinen
  * @since 19.7.2019, v0.1+
  */
object Profiler
{
	/**
	  * Performs a profiling based on new data in the database (new entity data or new segment filtering)
	  * @param connection DB connection
	  */
	def run()(implicit connection: Connection): Unit =
	{
		// Handles each entity type separately
		Entity.typeIds.foreach { typeId =>
			
			val allTargetIds = Entities.ids.forTypeWithId(typeId).get
			val allSegmentIds = Profiling.segments.ids.forContentTypeWithId(typeId)
			
			// println(s"Matching targets [${allTargetIds.mkString(", ")}] against segments [${allSegmentIds.mkString(", ")}]")
			
			// Performs a profiling for each segment available (caches entity data between segments)
			allSegmentIds.foldLeft(Map[Int, ReadData]()) { (cached, segmentId) =>
				profileSegment(segmentId, allTargetIds, cached) }
		}
	}
	
	private def profileSegment(segmentId: Int, allTargetIds: Iterable[Int],
							   cachedData: Map[Int, ReadData])(implicit connection: Connection) =
	{
		// Won't perform any profiling without filter data
		Profiling.segment(segmentId).latestPartialFilter.map { partialFilter =>
			
			// Finds the last profiling for the specified segment, as well as the currently used filter (partial)
			val lastProfiling = Profiling.segment(segmentId).latestProfiling
			// println(s"Using filter ${partialFilter.id}. Last profiling was made: ${
			//	lastProfiling.map { _.time.toString }.getOrElse("Never")}")
			
			// If filter was changed since last profiling, or if there hasn't been any profiling yet, updates data
			// for all target entities
			if (lastProfiling.forall { _.filterId != partialFilter.id })
				reprofile(segmentId, allTargetIds, cachedData, partialFilter)
			// Otherwise only updates entities which were updated after last profiling
			else
				update(lastProfiling.get, allTargetIds, cachedData, partialFilter)
			
		}.getOrElse(cachedData)
	}
	
	private def update(lastProfiling: ProfilingEvent, allTargetIds: Iterable[Int], cachedData: Map[Int, ReadData],
					   partialFilter: PartialSegmentFilter)(implicit connection: Connection) =
	{
		// println("Updating last profiling")
		
		// Finds out all entities which had their data updated after the last profiling event
		val missingReads = allTargetIds.filterNot { cachedData.contains }.flatMap {
			dataForEntity(_, Some(lastProfiling.time)) }
		val cachedUpdates = cachedData.filter { _._2.lastRead.readTime > lastProfiling.time }
		val updates = missingReads ++ cachedUpdates
		
		// Filters target data and finds out which of the updated targets belong to segment
		// (only some of the entities were updated)
		if (updates.nonEmpty)
		{
			val filter = Profiling.complete(partialFilter)
			val (notBelongs, belongs) = updates.divideBy { d => filter(d._2.readData) }
			val notBelongsIds = notBelongs.map { _._1 }.toSet
			val belongsIds = belongs.map { _._1 }.toSet
			
			// Copies last profiling results, but may remove some entities and add others
			val oldTargetIds = Profiling(lastProfiling.id).contentIds.toSet
			val newTargetIds = oldTargetIds -- notBelongsIds ++ belongsIds
			
			// Saves the new profiling to the database
			Profiling.insert(lastProfiling.segmentId, filter.id, newTargetIds)
		}
		
		// Returns updated cached data
		cachedData ++ missingReads
	}
	
	private def reprofile(segmentId: Int, allTargetIds: Iterable[Int], cachedData: Map[Int, ReadData],
						  partialFilter: PartialSegmentFilter)(implicit connection: Connection) =
	{
		// println("Creating a completely new profiling")
		
		// Finds all missing entity data
		val data = allTargetIds.flatMap { targetId =>
			
			if (cachedData.contains(targetId))
				Some(targetId -> cachedData(targetId))
			else
				dataForEntity(targetId)
		}
		
		// println(s"Using entity data: \n${data.map { case (id, data) => s"$id: ${data.readData}" }.mkString("\n")}")
		
		// Completes the filter and applies it to all targets
		val filter = Profiling.complete(partialFilter)
		val newSegmentTargetIds = data.filter { case (_, data) => filter(data.readData) }.map { _._1 }.toSet
		
		// println(s"Used filter: $filter")
		// println(s"Entities that were accepted by filter: [${newSegmentTargetIds.mkString(",")}]")
		
		// Saves a new profiling to the database
		Profiling.insert(segmentId, filter.id, newSegmentTargetIds)
		
		// Returns new cached data
		data.toMap
	}
	
	private def dataForEntity(entityId: Int, after: Option[Instant] = None)(implicit connection: Connection) =
	{
		val accessPoint = Entity(entityId)
		val read = after.map(accessPoint.latestReadAfter).getOrElse(accessPoint.latestRead)
		read.map { r => entityId -> ReadData(r, database.DataRead(r.id).data) }
	}
}

private case class ReadData(lastRead: DataRead, readData: DataSet)