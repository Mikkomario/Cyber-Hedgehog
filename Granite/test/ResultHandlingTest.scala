import java.nio.file.Paths

import utopia.flow.util.TimeExtensions._
import utopia.flow.generic.ValueConversions._
import ch.database.{Company, ConnectionPool}
import ch.granite.controller.{QueryParser, ResultHandler}
import ch.granite.database.{Fields, Mappings}
import ch.util.ThreadPool
import utopia.flow.generic.DataType
import utopia.flow.parse.JSONReader

import scala.concurrent.ExecutionContext

/**
  * Completely handles a test result
  * @author Mikko Hilpinen
  * @since 13.7.2019, v0.1+
  */
object ResultHandlingTest extends App
{
	DataType.setup()
	
	val serviceId = 8
	
	// Parses the result first
	val result = QueryParser(
		JSONReader(Paths.get("test-data/example-response-with-sections.json").toFile).get.getModel)
	
	println(result)
	
	assert(result.baseValues.nonEmpty)
	assert(result.multiSelectIds.nonEmpty)
	assert(result.dropDownIds.nonEmpty)
	
	implicit val exc: ExecutionContext = ThreadPool.executionContext
	ConnectionPool { implicit connection =>
		
		// Finds field & option data
		val fields = Fields.forService(serviceId)
		val options = Fields.options.forService(serviceId)
		
		assert(fields.nonEmpty)
		assert(options.nonEmpty)
		
		// Finds mapping data
		val companyMappings = Mappings.company(fields, options)
		val contactMappings = Mappings.contact(fields, options)
		val roleMappings = Mappings.role(options)
		
		assert(companyMappings.nonEmpty)
		assert(contactMappings.nonEmpty)
		assert(roleMappings.nonEmpty)
		
		assert(companyMappings.exists { _.label.isIdentifier })
		assert(contactMappings.exists { _.label.isIdentifier })
		
		// Makes sure company identifier can be mapped
		val yCodeMapping = companyMappings.find { _.label.id == 5 }
		assert(yCodeMapping.isDefined)
		assert(yCodeMapping.get(result).getString == "2878928-5")
		
		// Makes sure contact role can be mapped
		val rolesIds = roleMappings.filter { _(result).contains(true) }.map { _.roleId }
		assert(rolesIds.nonEmpty)
		
		// Checks data before handling the result
		val latestReadBefore = Company.lastRead
		val existingTargetCompanyId = Company.idForIdentifier(5, "2878928-5")
		
		// Handles the result
		ResultHandler(result, companyMappings, contactMappings, roleMappings)
		
		// Checks data after handling the result
		val latestReadNow = Company.lastRead
		val currentTargetCompanyId = Company.idForIdentifier(5, "2878928-5")
		
		assert(latestReadNow.isDefined)
		assert(latestReadBefore.forall { old => latestReadNow.get.readTime > old.readTime })
		
		assert(currentTargetCompanyId.isDefined)
		assert(existingTargetCompanyId.forall { _ == currentTargetCompanyId.get })
	}
	
	println("Success!")
}
