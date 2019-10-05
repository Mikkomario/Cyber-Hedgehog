package ch.granite.controller

import java.time.Instant

import ch.granite.model.QueryResult
import ch.util.Log
import utopia.flow.datastructure.immutable.Value
import utopia.flow.datastructure.template
import utopia.flow.datastructure.template.Property
import utopia.flow.generic.StringType

import scala.collection.immutable.VectorBuilder

/**
  * Parses data query (form answer with sections) results into field-value pairs
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
object QueryParser
{
	/**
	  * Parses a form answer response JSON into field-value pairs
	  * @param resultJson Form answer response JSON
	  * @return A parsed result based from response
	  */
	def apply(resultJson: template.Model[Property]) =
	{
		val basicFieldBuffer = new VectorBuilder[(Int, Value)]
		val dropDownBuffer = new VectorBuilder[(Int, Option[Int])]
		val multiSelectBuffer = new VectorBuilder[(Int, Vector[Int])]
		
		// Parses each value of "fields" (array) in "sections" (array)
		val fields = resultJson("sections").getVector.flatMap { _("fields").getVector.flatMap { _.model } }
		if (fields.isEmpty)
			Log.warning(s"Couldn't find any fields (sections.fields) from $resultJson")
		
		fields.foreach
		{
			field =>
				val id = field("id").int
				if (id.isDefined)
				{
					val value = field("value")
					field("type").getString.toLowerCase match
					{
						case "multiselect" => multiSelectBuffer += (id.get -> value.getVector.flatMap { _("id").int })
						case "dropdown" => dropDownBuffer += (id.get -> value("id").int)
						case _ =>
							// Empty strings are recorded as empty values
							if (value.dataType == StringType && value.string.contains(""))
								basicFieldBuffer += (id.get -> Value.emptyWithType(StringType))
							else
								basicFieldBuffer += (id.get -> value)
					}
				}
				else
					Log.warning(s"Expected a field id when parsing result. Field: $field")
		}
		
		// Data origin time is read from the "created" field
		val creationTime = resultJson("created").instant
		if (creationTime.isEmpty)
			Log.warning(s"Failed to parse data creation time for response ${resultJson("id")}")
		
		// Creates a result object
		QueryResult(basicFieldBuffer.result().toMap, dropDownBuffer.result().toMap, multiSelectBuffer.result().toMap,
			creationTime getOrElse Instant.now())
	}
}
