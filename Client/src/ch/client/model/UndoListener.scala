package ch.client.model

/**
 * A trait for objects that are interested when data changes due to undo / redo features
 * @author Mikko Hilpinen
 * @since 9.11.2019, v3+
 */
trait UndoListener
{
	/**
	 * A function that should be called on undo / redo
	 */
	def onDataChanged()
}
