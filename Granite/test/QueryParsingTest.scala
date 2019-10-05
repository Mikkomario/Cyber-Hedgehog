import java.nio.file.Paths

import ch.granite.controller.QueryParser
import utopia.flow.generic.DataType
import utopia.flow.parse.JSONReader

/**
  * Tests QueryParser
  * @author Mikko Hilpinen
  * @since 13.7.2019, v0.1+
  */
object QueryParsingTest extends App
{
	DataType.setup()
	
	// Reads the JSON file first
	val data = JSONReader(Paths.get("test-data/example-response-with-sections.json").toFile).get.getModel
	
	assert(data.nonEmpty)
	
	// Then parses it
	val result = QueryParser(data)
	println(result)
	
	// Checks some results
	assert(result.baseValues(37).getString == "Suomen Tietosuojaturva")
	assert(result.baseValues(40).getString == "2878928-5")
	assert(result.baseValues(125).getBoolean)
	
	assert(result.dropDownIds(69).contains(2))
	
	val multiIds = result.multiSelectIds(81)
	assert(multiIds.contains(47))
	assert(multiIds.contains(50))
	assert(multiIds.contains(192))
	
	println("Success!")
}
