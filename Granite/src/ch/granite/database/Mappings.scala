package ch.granite.database

import utopia.flow.generic.ValueConversions._
import utopia.vault.sql.Extensions._
import ch.granite.database.model.{CompanyLabelMapping, ContactLabelMapping, ContactRoleMapping, LabelMappingFactory}
import ch.granite.model.{Field, SelectOption}
import utopia.vault.database.Connection
import utopia.vault.sql.ConditionElement

/**
  * Used for interacting with Granite field mappings in DB
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
object Mappings
{
	/**
	  * @param fields Available fields
	  * @param options Available select options
	  * @param connection DB connection
	  * @return Company label mappings for the specified fields & options
	  */
	def company(fields: Seq[Field], options: Seq[SelectOption])(implicit connection: Connection) =
		forFields(fields, options, CompanyLabelMapping)
	
	/**
	  * @param fields Available fields
	  * @param options Available select options
	  * @param connection DB connection
	  * @return Contact label mappings for the specified fields & options
	  */
	def contact(fields: Seq[Field], options: Seq[SelectOption])(implicit connection: Connection) =
		forFields(fields, options, ContactLabelMapping)
	
	/**
	  * @param options Available select options
	  * @param connection DB connection
	  * @return Contact role mappings for the specified options
	  */
	def role(options: Seq[SelectOption])(implicit connection: Connection) =
	{
		ContactRoleMapping.getMany(ContactRoleMapping.table("optionField").in(options.map { _.id: ConditionElement }))
			.flatMap { _.complete(options) }
	}
	
	private def forFields(fields: Seq[Field], options: Seq[SelectOption],
						  factory: LabelMappingFactory[_])(implicit connection: Connection) =
	{
		// First finds all associated mapping instances
		val fieldCondition = factory.fieldColumn.in(fields.map { _.id: ConditionElement })
		val optionCondition = factory.optionColumn.in(options.map { _.id: ConditionElement })
		
		val mappings = factory.getMany(fieldCondition || optionCondition)
		
		// Finally completes the mappings using field & option data
		mappings.flatMap { _.complete(fields, options) }
	}
}
