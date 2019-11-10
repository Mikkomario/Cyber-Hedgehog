package ch.client.view

import java.awt.event.KeyEvent

import utopia.flow.util.CollectionExtensions._
import utopia.reflection.shape.LengthExtensions._
import ch.client.model.{ColorScheme, EditableLabel, Margins, UserSettings}
import ch.client.util.Settings
import ch.database.{ConnectionPool, Entities, Entity}
import ch.model.DescribedEntityLabel
import ch.util.Log
import utopia.flow.datastructure.mutable.PointerWithEvents
import utopia.flow.event.{ChangeEvent, ChangeListener, Changing}
import utopia.genesis.color.Color
import utopia.genesis.event.KeyStateEvent
import utopia.genesis.handling.KeyStateListener
import utopia.genesis.shape.Axis.X
import utopia.reflection.component.swing.StackableAwtComponentWrapperWrapper
import utopia.reflection.component.swing.label.TextLabel
import utopia.reflection.container.stack.segmented.SegmentedGroup
import utopia.reflection.container.swing.{AwtContainerRelated, ScrollView, SegmentedRow, Stack}
import utopia.reflection.controller.data.StackContentManager
import utopia.reflection.localization.Localizer
import utopia.reflection.shape.StackLength
import utopia.reflection.util.Alignment.Center
import utopia.reflection.util.{ComponentContext, ComponentContextBuilder}
import utopia.vault.database.Connection

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Shows and handles editing of all entity labels
 * @author Mikko Hilpinen
 * @since 30.10.2019, v3+
 */
class EntityLabelsVC(val entityTypeId: Int)(implicit baseContextBuilder: ComponentContextBuilder, settings: UserSettings,
											margins: Margins, colorScheme: ColorScheme, exc: ExecutionContext)
	extends StackableAwtComponentWrapperWrapper with AwtContainerRelated
{
	// ATTRIBUTES	-------------------------------
	
	private implicit val originLanguage: String = Settings.sourceLanguageCode
	private implicit val localizer: Localizer = settings.localizer
	private implicit val baseContext: ComponentContext = baseContextBuilder.result
	private val headerContext = baseContextBuilder.copy(textColor = Color.white, textAlignment = Center,
		insideMargins = StackLength.any.withLowPriority x margins.small.downscaling).result
	
	private val segmentGroup = new SegmentedGroup(X)
	
	private val rowStack = Stack.column[EntityLabelRowVC](margin = baseContext.stackMargin)
	
	private val _view = Stack.buildColumn(margin = 0.fixed) { s =>
		// Adds header, then scroll view and add-label view at bottom
		s += SegmentedRow.partOfGroupWithItems(segmentGroup, Vector("Field Name", "Field Type", "Email",
			"ID").map { text => TextLabel.contextual(text)(headerContext) },
			margin = baseContext.relatedItemsStackMargin.downscaling).framed(
			margins.medium.any x 0.fixed, colorScheme.primary.light)
		s += ScrollView.contextual(rowStack).framed(margins.medium.any x margins.small.any, colorScheme.gray.light)
		s += new AddEntityLabelVC().view
	}
	
	private val contentManager = new StackContentManager[EditableLabel, EntityLabelRowVC](rowStack,
		l => new EntityLabelRowVC(segmentGroup, l))
	
	// Label data is read from DB
	private var labels = ConnectionPool.tryWith { implicit c => readData() } match
	{
		case Success(l) => l
		case Failure(e) => Log(e, "Failed to read entity labels from DB"); Vector()
	}
	private val _isRedoable = new PointerWithEvents[Boolean](false)
	private val _hasChanged = new PointerWithEvents[Boolean](false)
	
	
	// COMPUTED	-----------------------------------
	
	/**
	 * @return A pointer that shows whether these labels have changed (read only)
	 */
	def hasChangedPointer: Changing[Boolean] = _hasChanged
	
	/**
	 * @return Whether these labels have changed from the original
	 */
	def hasChanged = hasChangedPointer.value
	
	/**
	 * @return A pointer that shows whether there is a redoable state in these labels (read only)
	 */
	def isRedoablePointer: Changing[Boolean] = _isRedoable
	
	/**
	 * @return Whether there are redoable changes available
	 */
	def isRedoable = isRedoablePointer.value
	
	
	// INITIAL CODE	-------------------------------
	
	// Displays data
	contentManager.content = labels
	// Listens to changes in labels
	labels.foreach { _.addChangeListener(LabelChangeHandler) }
	// Listens to key events
	addKeyStateListener(KeyHandler)
	
	
	// IMPLEMENTED	-------------------------------
	
	override protected def wrapped = _view
	
	override def component = _view.component
	
	
	// OTHER	-----------------------------------
	
	/**
	 * Undoes the latest change in these labels (if any)
	 */
	def undo() = labels.flatMap { l => l.lastChangeTime.map { t => l -> t } }.maxByOption { _._2 }
		.foreach { _._1.undoLastChange() }
	
	/**
	 * Redoes the latest undone change in these labels (if any are available)
	 */
	def redo() = labels.flatMap { l => l.lastUndoTime.map { t => l -> t } }.maxByOption { _._2 }.foreach { _._1.redo() }
	
	def push() =
	{
		ConnectionPool.tryWith { implicit connection =>
			// Updates all changed labels to DB
			labels.foreach { l =>
				if (l.descriptionIsChanged)
					l.languageId.foreach { lang => Entity.label(l.id).description.insert(lang, l.name, l.description) }
				if (l.configurationIsChanged)
					Entity.label(l.id).insertConfiguration(l.dataType, l.isIdentifier, l.isEmail)
			}
			// Finally loads data from DB
			labels = readData()
			_hasChanged.value = false
			_isRedoable.value = false
			
		}.failure.foreach { e => Log(e, "Failed to push label data to DB") }
	}
	
	// Reads new versions of data from DB
	private def readData()(implicit connection: Connection) = Entities.labels.forTypeWithId(entityTypeId).get.map {
		l => DescribedEntityLabel(l, Entity.label(l.id).descriptions(settings.languages.map { _.id })) }.map { new EditableLabel(_) }
	
	
	// NESTED	-----------------------------------
	
	object LabelChangeHandler extends ChangeListener[Any]
	{
		// When labels change, updates some pointers
		override def onChangeEvent(event: ChangeEvent[Any]) =
		{
			_hasChanged.value = labels.exists { _.isChanged }
			_isRedoable.value = labels.exists { _.isRedoable }
		}
	}
	
	object KeyHandler extends KeyStateListener with utopia.inception.handling.immutable.Handleable
	{
		// Is only interested in ctrl combo keys
		override def keyStateEventFilter = KeyStateEvent.wasPressedFilter && KeyStateEvent.controlDownFilter
		
		override def onKeyState(event: KeyStateEvent) = event.index match
		{
			case KeyEvent.VK_Z =>
				if (event.keyStatus(KeyEvent.VK_SHIFT))
					redo() // Ctrl + Shift + Z = Redo
				else
					undo() // Ctrl + Z = Undo
			case KeyEvent.VK_P => push() // Ctrl + P = Push
			case _ => Unit
		}
	}
}
