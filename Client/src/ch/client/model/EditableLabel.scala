package ch.client.model

import utopia.flow.util.CollectionExtensions._
import ch.model.{DataType, DescribedEntityLabel}

/**
 * This version of label is mutable and editable by the user, it contains all changes until they are uploaded
 * @author Mikko Hilpinen
 * @since 9.11.2019, v3+
 */
class EditableLabel(val original: DescribedEntityLabel)
{
	// ATTRIBUTES	---------------------
	
	private val _name = new ChangeList(original.name.getOrElse(""))
	private val _type = new ChangeList(original.dataType)
	private val _isEmail = new ChangeList(original.isEmail)
	private val _isIdentifier = new ChangeList(original.isIdentifier)
	
	private val fields = Vector(_name, _type, _isEmail, _isIdentifier)
	
	private var undoListeners = Vector[UndoListener]()
	
	
	// COMPUTED	-------------------------
	
	/**
	 * @return Current label name
	 */
	def name = _name.current
	def name_=(newName: String) = _name.set(newName)
	
	/**
	 * @return Current label data type
	 */
	def dataType = _type.current
	def dataType_=(newType: DataType) = _type.set(newType)
	
	/**
	 * @return Whether label currently represents an email
	 */
	def isEmail = _isEmail.current
	def isEmail_=(isEmail: Boolean) = _isEmail.set(isEmail)
	
	/**
	 * @return Whether label is currently marked as an identifier
	 */
	def isIdentifier = _isIdentifier.current
	def isIdentifier_=(isIdentifier: Boolean) = _isIdentifier.set(isIdentifier)
	
	/**
	 * @return Last time a value in this label was changed
	 */
	def lastChangeTime = fields.flatMap { _.lastChangeTime }.maxOption
	/**
	 * @return Last time a change in this label was undone (only returns times for changes that can be redone)
	 */
	def lastUndoTime = fields.flatMap { _.lastUndoTime }.maxOption
	
	
	// OTHER	-------------------------
	
	/**
	 * Undoes the last change in this label
	 */
	def undoLastChange() =
	{
		// Finds the last field that was changed and undoes the last change in that field
		val changedFields = fields.map { f => f -> f.lastChangeTime }.filter { _._2.isDefined }
		if (changedFields.nonEmpty)
		{
			changedFields.maxBy { _._2.get }._1.undo()
			undoListeners.foreach { _.onDataChanged() }
		}
	}
	
	/**
	 * Redoes the last undone change in this label
	 */
	def redo() =
	{
		// Finds the last field that was undone and redoes it
		val targetFields = fields.map { f => f -> f.lastUndoTime }.filter { _._2.isDefined }
		if (targetFields.nonEmpty)
		{
			targetFields.maxBy { _._2.get }._1.redo()
			undoListeners.foreach { _.onDataChanged() }
		}
	}
	
	/**
	 * Registers new undo listener
	 * @param listener A new listener to be informed of undo / redo changes
	 */
	def addUndoListener(listener: UndoListener) = undoListeners :+= listener
	
	/**
	 * Removes an undo listener
	 * @param listener A listener no longer interested in changes
	 */
	def removeUndoListener(listener: UndoListener) = undoListeners = undoListeners.filterNot { _ == listener }
}
