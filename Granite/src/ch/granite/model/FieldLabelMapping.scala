package ch.granite.model

import ch.model.EntityLabel
import utopia.flow.datastructure.immutable.Value

/**
 * Used for linking granite fields with entity labels
 * @author Mikko Hilpinen
 * @since 6.10.2019, v2+
 * @param id Unique id of this mapping
 * @param field Mapped field
 * @param label Mapped label
 */
case class FieldLabelMapping(id: Int, field: Field, override val label: EntityLabel) extends LabelMapping
{
	// IMPLEMENTED	-------------------------
	
	override def toString = s"Granite Field ${field.graniteId} -> Label ${label.id}"
	
	/**
	 * Finds the provided value from a query result
	 * @param result A query result
	 * @return Value read for the label in this mapping
	 */
	override def apply(result: QueryResult): Option[Value] = result.baseValues.get(field.graniteId)
}
