package ch.model

import java.time.Instant

/**
  * Represents a single event when (new) data is read from a source
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  * @param id The unique identifier for this read event
  * @param sourceId The identifier of the source from which the data was read
  * @param targetId The identifier for the entity for which the data was read / linked to
  * @param dataOriginTime The time when the read data was generated
  * @param readTime The time when the data was read
  */
case class DataRead(id: Int, sourceId: Int, targetId: Int, dataOriginTime: Instant, readTime: Instant)
