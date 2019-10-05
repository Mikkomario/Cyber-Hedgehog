package ch.model.profiling

import java.time.Instant

/**
  * Represents a company profiling performed at some point in time
  * @author Mikko Hilpinen
  * @since 17.7.2019, v0.1+
  * @param id Unique id for this profiling
  * @param segmentId Id of targeted segment
  * @param time Time when this profiling was performed
  */
case class ProfilingEvent(id: Int, segmentId: Int, filterId: Int, time: Instant)
