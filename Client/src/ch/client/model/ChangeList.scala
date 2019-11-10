package ch.client.model

import java.time.Instant

import utopia.flow.event.Changing

/**
 * This mutable value keeps track of all changes and can undo or redo them when requested
 * @author Mikko Hilpinen
 * @since 9.11.2019, v3+
 * @param original The original (immutable) value
 */
class ChangeList[A](private val original: A) extends Changing[A]
{
	// ATTRIBUTES	----------------------
	
	private var activeChanges = Vector[(A, Instant)]()
	private var lastUndos = Vector[(A, Instant)]()
	
	
	// COMPUTED	--------------------------
	
	/**
	 * @return Current value of this changing entity
	 */
	def current = activeChanges.lastOption.map { _._1 }.getOrElse(original)
	
	/**
	 * @return The last time value was changed (None if never changed)
	 */
	def lastChangeTime = activeChanges.lastOption.map { _._2 }
	
	/**
	 * @return The last time a revertible change was undone (None if no such change available)
	 */
	def lastUndoTime = lastUndos.lastOption.map { _._2 }
	
	/**
	 * @return Whether this value has changed from its original
	 */
	def isChanged = activeChanges.nonEmpty
	/**
	 * @return Whether this list is in a state where redo is available
	 */
	def isRedoable = lastUndos.nonEmpty
	
	
	// IMPLEMENTED	----------------------
	
	override def value = current
	

	// OTHER	--------------------------
	
	/**
	 * @param newValue New value for this entity
	 */
	def set(newValue: A) =
	{
		// Will not record changes that don't alter the value
		if (!activeChanges.lastOption.contains(newValue))
		{
			val oldValue = current
			activeChanges :+= (newValue, Instant.now())
			// Redo becomes unavailable after field has been changed again
			lockChanges()
			fireChangeEvent(oldValue)
		}
	}
	
	/**
	 * Undoes the last change (if possible)
	 */
	def undo() =
	{
		if (activeChanges.nonEmpty)
		{
			val oldValue = current
			val undone = activeChanges.last._1
			activeChanges = activeChanges.dropRight(1)
			lastUndos :+= (undone, Instant.now())
			fireChangeEvent(oldValue)
		}
	}
	
	/**
	 * Redoes the last change (if possible)
	 */
	def redo() =
	{
		// TODO: WET WET
		if (lastUndos.nonEmpty)
		{
			val oldValue = current
			val redone = lastUndos.last._1
			lastUndos = lastUndos.dropRight(1)
			activeChanges :+= (redone, Instant.now())
			fireChangeEvent(oldValue)
		}
	}
	
	/**
	 * Locks current changes, making redo unavailable
	 */
	def lockChanges() =
	{
		if (lastUndos.nonEmpty)
			lastUndos = Vector()
	}
}
