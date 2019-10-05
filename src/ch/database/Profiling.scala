package ch.database

import utopia.flow.generic.ValueConversions._
import utopia.flow.util.CollectionExtensions._
import ch.database.model.{CompanySegmentConnection, SegmentFilter, SegmentFilterCondition, SegmentFilterConditionCombo}
import ch.model.profiling.condition.{PartialSegmentFilter, PartialSegmentFilterConditionCombo}
import ch.model.profiling.{ProfilingEvent, ProfilingSegment}
import ch.util.Log
import utopia.vault.database.Connection
import utopia.vault.model.immutable.access.{IntIdAccess, ItemAccess, ManyAccessWithIds, ManyIdAccess, SingleAccess}
import utopia.vault.sql.{Select, Where}

/**
  * Used for interacting with company profiling DB data
  * @author Mikko Hilpinen
  * @since 17.7.2019, v0.1+
  */
object Profiling extends SingleAccess[Int, ProfilingEvent]
{
	// IMPLEMENTED	-------------------
	
	override protected def idValue(id: Int) = id
	
	override def factory = model.Profiling
	
	
	// COMPUTED	-----------------------
	
	/**
	  * @return Individual segment access
	  */
	def segment = Segment
	/**
	  * @return Multiple segment access
	  */
	def segments = Segments
	
	
	// IMPLEMENTED	-------------------
	
	override def apply(id: Int) = new SingleProfiling(id)
	
	
	// OTHER	-----------------------
	
	/**
	  * @param connection DB connection
	  * @return ids of all recorded segments
	  */
	@deprecated("Replaced with Segments.ids.all", "v1.1")
	def segmentIds(implicit connection: Connection) = Tables.segment.allIndices.flatMap { _.int }
	
	/**
	  * Returns the latest profiling event for the targeted segment
	  * @param segmentId Id of targeted segment
	  * @param connection DB connection
	  */
	@deprecated("Replaced with segment(<id>).latestProfiling", "v1.1")
	def latestProfilingForSegment(segmentId: Int)(implicit connection: Connection) =
		model.Profiling.withSegmentId(segmentId).searchMax("created")
	
	/**
	  * Finds the latest (current) filter used for the specified segment. Filter data is incomplete and doesn't contain
	  * data conditions
	  * @param segmentId Target segment id
	  * @param connection DB connection
	  * @return Filter used for the segment (partial)
	  */
	@deprecated("Replaced with segment(<id>).latestPartialFilter", "v1.1")
	def latestPartialFilterForSegment(segmentId: Int)(implicit connection: Connection) =
		SegmentFilter.getMax("created", SegmentFilter.withSegmentId(segmentId).toCondition)
	
	/**
	  * Completes a partially read filter
	  * @param filter A partial filter
	  * @param connection DB connection
	  * @return A completed filter
	  */
	def complete(filter: PartialSegmentFilter)(implicit connection: Connection) =
	{
		// Proceeds to find all sub-conditions for the filter
		val directChildConditions = SegmentFilterCondition.getMany(SegmentFilterCondition.withFilterId(filter.id).toCondition)
		val directComboConditions = SegmentFilterConditionCombo.getMany(SegmentFilterConditionCombo.withFilterId(filter.id).toCondition)
		
		val startIds = directComboConditions.map { _.id }.toSet
		val completedComboConditions = directComboConditions.map { c => completeCondition(c, startIds) }
		
		filter.withChildren(directChildConditions ++ completedComboConditions)
	}
	
	/**
	  * Reads the latest (current) filter for the specified segment
	  * @param segmentId Target segment id
	  * @param connection DB connection
	  * @return Filter used for the segment
	  */
	@deprecated("Replaced with segment(<id>).latestFilter", "v1.1")
	def latestFilterForSegment(segmentId: Int)(implicit connection: Connection) =
		segment(segmentId).latestPartialFilter.map(complete)
	
	/**
	  * Finds ids for all companies that were associated with a segment in the specified profiling
	  * @param profilingId Id of targeted profiling
	  * @param connection DB connection
	  */
	@deprecated("Replaced with apply(<id>).companyIds", "v1.1")
	def companyIdsForProfiling(profilingId: Int)(implicit connection: Connection) =
	{
		val table = Tables.companySegmentConnection
		connection(Select(table, "company") + Where(CompanySegmentConnection.withProfilingId(profilingId).toCondition))
			.rows.map { _.value }.flatMap { _.int }
	}
	
	/**
	  * Inserts a new profiling to DB
	  * @param segmentId Target segment's id
	  * @param filterId Used filter's id
	  * @param segmentCompanyIds Ids of companies that now belong to the segment
	  * @param connection DB connection
	  */
	def insert(segmentId: Int, filterId: Int, segmentCompanyIds: Set[Int])(implicit connection: Connection) =
	{
		// Inserts the profiling first
		val profilingId = model.Profiling.forInsert(segmentId, filterId).insert().getInt
		
		// Then connects the profiling with all specified companies
		segmentCompanyIds.foreach { CompanySegmentConnection.forInsert(profilingId, _).insert() }
	}
	
	private def completeCondition(part: PartialSegmentFilterConditionCombo, visitedIds: Set[Int])
								 (implicit connection: Connection): ch.model.profiling.condition.SegmentFilterConditionCombo =
	{
		// Finds direct children, completes them if necessary
		val directChildConditions = SegmentFilterCondition.getMany(SegmentFilterCondition.withParentId(part.id).toCondition)
		val directComboConditions = SegmentFilterConditionCombo.getMany(SegmentFilterConditionCombo.withParentId(
			part.id).toCondition)
		
		// Will filter out looping combos and warn about them
		val (nonLooping, looping) = directComboConditions.divideBy { c => visitedIds.contains(c.id) }
		if (looping.nonEmpty)
			Log.warning(s"Following SegmentFilterConditionCombinations contain reference cycles: [${
				looping.map { _.id }.mkString(", ")}]")
		
		// Completes the combo conditions
		val newVisitedIds = visitedIds ++ nonLooping.map { _.id }
		val completedComboConditions = nonLooping.map { c => completeCondition(c, newVisitedIds) }
		
		part.withChildren(directChildConditions ++ completedComboConditions)
	}
	
	
	// NESTED	------------------------
	
	class SingleProfiling(id: Int) extends ItemAccess[ProfilingEvent](id, factory)
	{
		/**
		  * Finds ids for all companies that were associated with a segment in this profiling
		  * @param connection DB connection
		  */
		def companyIds(implicit connection: Connection) =
		{
			connection(Select(Tables.companySegmentConnection, "company") +
				Where(CompanySegmentConnection.withProfilingId(id).toCondition))
				.rows.map { _.value }.flatMap { _.int }
		}
	}
	
	object SegmentIds extends ManyIdAccess[Int] with IntIdAccess
	{
		override def table = model.ProfilingSegment.table
	}
	
	object Segments extends ManyAccessWithIds[Int, ProfilingSegment, SegmentIds.type]
	{
		override def ids = SegmentIds
		
		override protected def idValue(id: Int) = id
		
		override def factory = model.ProfilingSegment
	}
	
	object Segment extends SingleAccess[Int, ProfilingSegment]
	{
		// IMPLEMENTED	-----------------
		
		override protected def idValue(id: Int) = id
		
		override def factory = model.ProfilingSegment
		
		override def apply(id: Int) = new SingleSegment(id)
		
		
		// NESTED	---------------------
		
		class SingleSegment(id: Int) extends ItemAccess[ProfilingSegment](id, factory)
		{
			/**
			  * Returns the latest profiling event for this segment
			  * @param connection DB connection
			  */
			def latestProfiling(implicit connection: Connection) =
				model.Profiling.withSegmentId(id).searchMax("created")
			
			/**
			  * Finds the latest (current) filter used for this segment. Filter data is incomplete and doesn't contain
			  * data conditions
			  * @param connection DB connection
			  * @return Filter used for the segment (partial)
			  */
			def latestPartialFilter(implicit connection: Connection) =
				SegmentFilter.getMax("created", SegmentFilter.withSegmentId(id).toCondition)
			
			/**
			  * Reads the latest (current) filter for this segment
			  * @param connection DB connection
			  * @return Filter used for the segment
			  */
			def latestFilter(implicit connection: Connection) = latestPartialFilter.map(complete)
		}
	}
}
