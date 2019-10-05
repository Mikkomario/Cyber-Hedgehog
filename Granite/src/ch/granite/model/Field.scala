package ch.granite.model

/**
  * Represents a granite field
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  * @param id This field's unique identifier
  * @param serviceId The granite service this field is part of
  * @param graniteFieldId The id of granite field (within service) this field represents
  */
case class Field(id: Int, serviceId: Int, graniteFieldId: Int)
