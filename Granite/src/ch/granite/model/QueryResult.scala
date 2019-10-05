package ch.granite.model

import java.time.Instant

import utopia.flow.datastructure.immutable.Value

/**
  * Used for storing parsed Granite query results
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  * @param baseValues Basic field id -> provided value
  * @param dropDownIds Drop down field id -> selected id
  * @param multiSelectIds Multi-select field id -> Selected ids
  * @param dataOriginTime The time when the data was generated (not read)
  */
case class QueryResult(baseValues: Map[Int, Value], dropDownIds: Map[Int, Option[Int]],
					   multiSelectIds: Map[Int, Vector[Int]], dataOriginTime: Instant)
{
	override def toString = s"Drop downs: [${
		dropDownIds.toVector.filter { _._2.isDefined }.map { case (key, value) => s"$key: $value" }.mkString(", ") }]\nMultiselect: [${
		multiSelectIds.map { case (key, value) => s"$key: [${value.mkString(", ")}]" }.mkString(", ") }]\nOther keys: [${baseValues.keys.mkString(", ")}]"
}
