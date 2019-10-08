package ch.granite.database

import utopia.flow.generic.ValueConversions._
import ch.database.model.{EntityLabel, EntityLabelConfiguration}
import utopia.vault.database.Connection
import utopia.vault.model.immutable.access.{ConditionalManyAccess, ItemAccess, SingleAccess}

/**
 * Used for accessing individual Granite service's data
 * @author Mikko Hilpinen
 * @since 7.10.2019, v2+
 */
object Service extends SingleAccess[Int, ch.granite.model.Service]
{
	// IMPLEMENTED	--------------------
	
	override protected def idValue(id: Int) = id
	
	override def factory = model.Service
	
	override def apply(id: Int) = new SingleService(id)
	
	
	// NESTED	------------------------
	
	/**
	 * Used for accessing an individual service's data
	 * @param id Service id
	 */
	class SingleService(id: Int) extends ItemAccess[ch.granite.model.Service](id, factory)
	{
		// COMPUTED	--------------------
		
		private def labelFactory = EntityLabel
		private def fieldFactory = model.Field
		private def optionFactory = model.SelectOption
		private def fieldMappingFactory = model.FieldLabelMapping
		private def optionMappingFactory = model.OptionLabelMapping
		private def linkMappingFactory = model.LinkTypeMapping
		private def labelConfigurationFactory = EntityLabelConfiguration
		
		private def labelConfigurationTable = labelConfigurationFactory.table
		
		private def notDeprecatedColumn = labelConfigurationTable("deprecatedAfter")
		
		private def notDeprecatedCondition = notDeprecatedColumn.isNull
		private def fieldServiceCondition = fieldFactory.withServiceId(id).toCondition
		
		/**
		 * @return Access point to all fields registered under this service
		 */
		def fields = new ServiceFieldsAccess
		
		/**
		 * @return Access point to all selection options registered under this service
		 */
		def options = new ServiceOptionsAccess
		
		
		// OTHER	--------------------
		
		private def labelOfTypeCondition(typeId: Int) = labelFactory.withTargetTypeId(typeId).toCondition
		
		
		// NESTED	--------------------
		
		/**
		 * Used for accessing fields under a service
		 */
		class ServiceFieldsAccess extends ConditionalManyAccess[ch.granite.model.Field](fieldServiceCondition, fieldFactory)
		{
			// COMPUTED	----------------
			
			/**
			 * @return Access point to all mappings linked to these fields
			 */
			def labelMappings = new ServiceFieldMappingsAccess
			
			
			// NESTED	----------------
			
			/**
			 * Used for accessing field label mappings under a service
			 */
			class ServiceFieldMappingsAccess extends ConditionalManyAccess[ch.granite.model.FieldLabelMapping](
				fieldServiceCondition && notDeprecatedCondition, fieldMappingFactory)
			{
				/**
				 * Searches mappings for a specific entity type
				 * @param typeId Id of searched entity type
				 * @param connection DB Connection
				 * @return All mappings in this group (service) linked to specified entity type
				 */
				def forEntityTypeWithId(typeId: Int)(implicit connection: Connection) =
					find(labelOfTypeCondition(typeId))
			}
		}
		
		/**
		 * Used for accessing selection options under a service
		 */
		class ServiceOptionsAccess extends ConditionalManyAccess[ch.granite.model.SelectOption](fieldServiceCondition,
			optionFactory)
		{
			// COMPUTED	------------------
			
			/**
			 * @return Access point to all option to label mappings linked to these options
			 */
			def labelMappings = new ServiceOptionMappingAccess
			
			/**
			 * @return Access point to all option to link type mappings linked to these options
			 */
			def linkTypeMappings = new ServiceOptionLinkMappingAccess
			
			
			// NESTED	------------------
			
			/**
			 * Used for accessing option label mappings under a service
			 */
			class ServiceOptionMappingAccess extends ConditionalManyAccess[ch.granite.model.OptionLabelMapping](
				fieldServiceCondition && notDeprecatedCondition, optionMappingFactory)
			{
				/**
				 * Searches for mappings linked to specific entity type
				 * @param typeId Id of targeted entity type
				 * @param connection DB Connection
				 * @return All option to label mappings for this service and specified entity type
				 */
				def forEntityTypeWithId(typeId: Int)(implicit connection: Connection) =
					find(labelOfTypeCondition(typeId))
			}
			
			/**
			 * Used for accessing option link type mappings under a service
			 */
			class ServiceOptionLinkMappingAccess extends ConditionalManyAccess[ch.granite.model.LinkTypeMapping](
				fieldServiceCondition, linkMappingFactory)
			{
				/**
				 * Searches for link type mappings that originate from a specific type of entity
				 * @param typeId Id of targeted entity type
				 * @param connection DB Connection
				 * @return All option to link type mappings for specified entity type
				 */
				def fromTypeWithId(typeId: Int)(implicit connection: Connection) =
					find(linkMappingFactory.withOriginTypeId(typeId).toCondition)
			}
		}
	}
}
