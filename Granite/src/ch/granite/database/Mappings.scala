package ch.granite.database

import ch.database.model.{EntityLabel, EntityLabelConfiguration}
import utopia.flow.generic.ValueConversions._
import utopia.vault.sql.Extensions._
import ch.granite.database.model.{CompanyLabelMapping, ContactLabelMapping, ContactRoleMapping, LabelMappingFactory}
import ch.granite.model.{Field, SelectOption}
import utopia.vault.database.Connection
import utopia.vault.model.immutable.access.{ConditionalManyAccess, ManyAccess}
import utopia.vault.sql.ConditionElement

/**
  * Used for interacting with Granite field mappings in DB
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
@deprecated("Replaced with Service", "v2")
object Mappings
{
	// COMPUTED	------------------------
	
	private def labelFactory = EntityLabel
	private def labelConfigurationFactory = EntityLabelConfiguration
	
	private def labelConfigurationTable = labelConfigurationFactory.table
	
	private def notDeprecatedColumn = labelConfigurationTable("deprecatedAfter")
	
	private def notDeprecatedCondition = notDeprecatedColumn.isNull
	
	
	// OTHER	------------------------
	
	/**
	  * @param fields Available fields
	  * @param options Available select options
	  * @param connection DB connection
	  * @return Company label mappings for the specified fields & options
	  */
	@deprecated("Replaced with Service(id).fields.labelMappings", "v2")
	def company(fields: Seq[Field], options: Seq[SelectOption])(implicit connection: Connection) =
		forFields(fields, options, CompanyLabelMapping)
	
	/**
	  * @param fields Available fields
	  * @param options Available select options
	  * @param connection DB connection
	  * @return Contact label mappings for the specified fields & options
	  */
	@deprecated("Replaced with Service(id).fields.labelMappings", "v2")
	def contact(fields: Seq[Field], options: Seq[SelectOption])(implicit connection: Connection) =
		forFields(fields, options, ContactLabelMapping)
	
	/**
	  * @param options Available select options
	  * @param connection DB connection
	  * @return Contact role mappings for the specified options
	  */
	@deprecated("Replaced with Service(id).options.linkTypeMappings", "v2")
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
	
	/*
	private def labelOfTypeCondition(typeId: Int) = labelFactory.withTargetTypeId(typeId).toCondition
	
	
	// NESTED	-----------------------
	
	object FieldToLabelMappings extends ConditionalManyAccess[ch.granite.model.FieldLabelMapping](
		notDeprecatedCondition, model.FieldLabelMapping)
	{
		// COMPUTED	-------------------
		
		def all(implicit connection: Connection) = get
		
		
		// OTHER	-------------------
		
		def forEntityTypeWithId(typeId: Int) = subGroup(labelOfTypeCondition(typeId))
	}
	
	object OptionToLabelMappings extends ConditionalManyAccess[ch.granite.model.OptionLabelMapping](notDeprecatedCondition,
		model.OptionLabelMapping)
	{
		// COMPUTED	------------------
		
		def all(implicit connection: Connection) = get
		
		
		// OTHER	------------------
		
		def forEntityTypeWithId(typeId: Int) = subGroup(labelOfTypeCondition(typeId))
	}
	
	object LinkTypeMappings extends ManyAccess[Int, ch.granite.model.LinkTypeMapping]
	{
		override protected def idValue(id: Int) = id
		
		override def factory = model.LinkTypeMapping
	}*/
}
