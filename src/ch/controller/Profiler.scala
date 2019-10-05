package ch.controller

import java.time.Instant

import utopia.flow.util.CollectionExtensions._
import utopia.flow.util.TimeExtensions._
import ch.database.{Company, Profiling}
import ch.model.{DataRead, DataSet}
import ch.model.profiling.condition.PartialSegmentFilter
import ch.model.profiling.ProfilingEvent
import utopia.vault.database.Connection

/**
  * Searches through company data and assigns companies to different segments based on a filter logic
  * @author Mikko Hilpinen
  * @since 19.7.2019, v0.1+
  */
object Profiler
{
	/**
	  * Performs a profiling based on new data in the database (new company data or new segment filtering)
	  * @param connection DB connection
	  */
	def run()(implicit connection: Connection): Unit =
	{
		val allCompanyIds = Company.allIds
		val allSegmentIds = Profiling.segments.ids.all
		
		// println(s"Matching companies [${allCompanyIds.mkString(", ")}] against segments [${allSegmentIds.mkString(", ")}]")
		
		// Performs a profiling for each segment available (caches company data between segments)
		allSegmentIds.foldLeft(Map[Int, CompanyReadData]()) { (cached, segmentId) =>
			profileSegment(segmentId, allCompanyIds, cached) }
	}
	
	private def profileSegment(segmentId: Int, allCompanyIds: Traversable[Int],
							   cachedCompanyData: Map[Int, CompanyReadData])(implicit connection: Connection) =
	{
		// Won't perform any profiling without filter data
		Profiling.segment(segmentId).latestPartialFilter.map { partialFilter =>
			
			// Finds the last profiling for the specified segment, as well as the currently used filter (partial)
			val lastProfiling = Profiling.segment(segmentId).latestProfiling
			// println(s"Using filter ${partialFilter.id}. Last profiling was made: ${
			//	lastProfiling.map { _.time.toString }.getOrElse("Never")}")
			
			// If filter was changed since last profiling, or if there hasn't been any profiling yet, updates data for all companies
			if (lastProfiling.forall { _.filterId != partialFilter.id })
				reprofile(segmentId, allCompanyIds, cachedCompanyData, partialFilter)
			// Otherwise only updates companies which were updated after last profiling
			else
				update(lastProfiling.get, allCompanyIds, cachedCompanyData, partialFilter)
			
		}.getOrElse(cachedCompanyData)
	}
	
	private def update(lastProfiling: ProfilingEvent, allCompanyIds: Traversable[Int],
					   cachedCompanyData: Map[Int, CompanyReadData], partialFilter: PartialSegmentFilter)
					  (implicit connection: Connection) =
	{
		// println("Updating last profiling")
		
		// Finds out all companies which had their data updated after the last profiling event
		val missingReads = allCompanyIds.filterNot { cachedCompanyData.contains }.flatMap {
			dataForCompany(_, Some(lastProfiling.time)) }
		val cachedUpdates = cachedCompanyData.filter { _._2.lastRead.readTime > lastProfiling.time }
		val updates = missingReads ++ cachedUpdates
		
		// Filters company data and finds out which of the updated companies belong to segment
		// (only some of the companies were updated)
		if (updates.nonEmpty)
		{
			val filter = Profiling.complete(partialFilter)
			val (notBelongs, belongs) = updates.divideBy { d => filter(d._2.readData) }
			val notBelongsIds = notBelongs.map { _._1 }.toSet
			val belongsIds = belongs.map { _._1 }.toSet
			
			// Copies last profiling results, but may remove some companies and add others
			val oldCompanyIds = Profiling(lastProfiling.id).companyIds.toSet
			val newCompanyIds = oldCompanyIds -- notBelongsIds ++ belongsIds
			
			// Saves the new profiling to the database
			Profiling.insert(lastProfiling.segmentId, filter.id, newCompanyIds)
		}
		
		// Returns updated cached data
		cachedCompanyData ++ missingReads
	}
	
	private def reprofile(segmentId: Int, allCompanyIds: Traversable[Int], cachedCompanyData: Map[Int, CompanyReadData],
						  partialFilter: PartialSegmentFilter)(implicit connection: Connection) =
	{
		// println("Creating a completely new profiling")
		
		// Finds all missing company data
		val companyData = allCompanyIds.flatMap { companyId =>
			
			if (cachedCompanyData.contains(companyId))
				Some(companyId -> cachedCompanyData(companyId))
			else
				dataForCompany(companyId)
		}
		
		// println(s"Using company data: \n${companyData.map { case (id, data) => s"$id: ${data.readData}" }.mkString("\n")}")
		
		// Completes the filter and applies it to all companies
		val filter = Profiling.complete(partialFilter)
		val newSegmentCompanyIds = companyData.filter { case (_, data) => filter(data.readData) }.map { _._1 }.toSet
		
		// println(s"Used filter: $filter")
		// println(s"Companies that were accepted by filter: [${newSegmentCompanyIds.mkString(",")}]")
		
		// Saves a new profiling to the database
		Profiling.insert(segmentId, filter.id, newSegmentCompanyIds)
		
		// Returns new cached data
		companyData.toMap
	}
	
	private def dataForCompany(companyId: Int, after: Option[Instant] = None)(implicit connection: Connection) =
		Company.lastReadForTargetId(companyId, after).map { read =>
			companyId -> CompanyReadData(read, Company.readData(read.id)) }
}

private case class CompanyReadData(lastRead: DataRead, readData: DataSet)