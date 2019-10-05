package ch.mailchimp.model

import java.time.Instant

/**
  * Represents a single mailChimp segment update event
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
case class SegmentUpdateEvent(id: Int, segmentId: Int, profilingId: Int, created: Instant)
