package ch.client.model

import java.time.Instant

/**
 * This mutable value keeps track of all changes and can undo or redo them when requested
 * @author Mikko Hilpinen
 * @since 9.11.2019, v3+
 * @param original The original (immutable) value
 */
class ChangeList[A](private val original: A)
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
	

	// OTHER	--------------------------
	
	/**
	 * @param newValue New value for this entity
	 */
	def set(newValue: A) =
	{
		// Will not record changes that don't alter the value
		if (!activeChanges.lastOption.contains(newValue))
		{
			activeChanges :+= (newValue, Instant.now())
			// Redo becomes unavailable after field has been changed again
			lockChanges()
		}
	}
	
	/**
	 * Undoes the last change (if possible)
	 */
	def undo() =
	{
		if (activeChanges.nonEmpty)
		{
			val undone = activeChanges.last._1
			activeChanges = activeChanges.dropRight(1)
			lastUndos :+= (undone, Instant.now())
		}
	}
	
	/**
	 * Redoes the last change (if possible)
	 */
	def redo() =
	{
		if (lastUndos.nonEmpty)
		{
			val redone = lastUndos.last._1
			lastUndos = lastUndos.dropRight(1)
			activeChanges :+= (redone, Instant.now())
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
