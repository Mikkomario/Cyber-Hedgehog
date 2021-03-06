package ch.granite.database

import ch.granite.database.model.SelectOption
import utopia.vault.database.Connection
import utopia.vault.nosql.access.ManyRowModelAccess
import utopia.vault.sql.SelectDistinct

/**
  * Used for interacting with granite field data in DB
  * @author Mikko Hilpinen
  * @since 14.7.2019, v0.1+
  */
object Fields extends ManyRowModelAccess[ch.granite.model.Field]
{
	// IMPLEMENTED	--------------
	
	override def globalCondition = None
	
	override def factory = model.Field
	
	
	// COMPUTED	------------------
	
	/**
	  * @return Access point to option fields
	  */
	def options = Options
	
	
	// OTHER	------------------
	
	/**
	  * Returns all unique service ids's recorded in DB
	  * @param connection DB connection
	  */
	@deprecated("Replaced with Service.ids.all", "v2")
	def serviceIds(implicit connection: Connection) = SelectDistinct(model.Field.table, "serviceId")
		.execute().rows.flatMap { _.value.int }
	
	/**
	  * @param serviceId id of targeted service
	  * @param connection DB connection
	  * @return All fields for that service
	  */
	@deprecated("Replaced with Services(id).fields", "v2")
	def forService(serviceId: Int)(implicit connection: Connection) =
		model.Field.getMany(serviceIdCondition(serviceId))
	
	/**
	  * @param serviceId id of targeted service
	  * @param connection DB connection
	  * @return All select options for that service
	  */
	@deprecated("Replaced with Services(id).options", "v2")
	def optionsForService(serviceId: Int)(implicit connection: Connection) =
		SelectOption.getMany(serviceIdCondition(serviceId))
	
	private def serviceIdCondition(serviceId: Int) = model.Field.withServiceId(serviceId).toCondition
	
	
	// NESTED	------------------
	
	object Options extends ManyRowModelAccess[ch.granite.model.SelectOption]
	{
		// IMPLEMENTED	----------
		
		override def globalCondition = None
		
		override def factory = model.SelectOption
		
		
		// OTHER	-------------
		
		/**
		  * @param serviceId id of targeted service
		  * @param connection DB connection
		  * @return All select options for that service
		  */
		@deprecated("Replaced with Services(id).options", "v2")
		def forService(serviceId: Int)(implicit connection: Connection) = find(serviceIdCondition(serviceId))
	}
}
