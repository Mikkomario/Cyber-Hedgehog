package ch.mailchimp.model

import java.time.Instant

/**
  * Represents a time when contact data was updated (sent to mailChimp)
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
case class ContactUpdateEvent(id: Int, listId: Int, created: Instant)
