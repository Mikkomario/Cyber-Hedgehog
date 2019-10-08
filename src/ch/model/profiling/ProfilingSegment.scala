package ch.model.profiling

/**
  * Represents a profiling target segment
  * @author Mikko Hilpinen
  * @since 30.7.2019, v1.1+
 *  @param id The unique id of this segment
 *  @param targetEntityTypeId Id of entity type this segment is part of
  */
case class ProfilingSegment(id: Int, targetEntityTypeId: Int)
