package ch.granite.model

import ch.model.DataLabel
import utopia.flow.datastructure.immutable.Value

/**
 * Common trait for granite data to label mappings
 * @author Mikko Hilpinen
 * @since 7.10.2019, v2+
 */
trait LabelMapping
{
	/**
	 * @return Label to which data is mapped
	 */
	def label: DataLabel
	
	/**
	 * @param result Granite read result
	 * @return Mapped label value read from the result. None if result didn't contain mapping target.
	 */
	def apply(result: QueryResult): Option[Value]
}
