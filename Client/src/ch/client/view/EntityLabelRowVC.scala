package ch.client.view

import utopia.reflection.shape.LengthExtensions._
import ch.client.model.{EditableLabel, UndoListener, UserSettings}
import ch.client.util.Settings
import ch.model.DataType.{BooleanType, StringType}
import ch.model.DataType
import utopia.reflection.component.Refreshable
import utopia.reflection.component.swing.{DropDown, StackableAwtComponentWrapperWrapper, Switch, TextField}
import utopia.reflection.container.stack.StackLayout.Fit
import utopia.reflection.container.stack.segmented.SegmentedGroup
import utopia.reflection.container.swing.SegmentedRow
import utopia.reflection.localization.{DisplayFunction, LocalizedString, Localizer}
import utopia.reflection.util.{ComponentContext, ComponentContextBuilder}

/**
 * Shows editable data for an entity label
 * @author Mikko Hilpinen
 * @since 25.10.2019, v3+
 */
class EntityLabelRowVC(val segmentGroup: SegmentedGroup, initialLabel: EditableLabel)
					  (implicit context: ComponentContextBuilder, settings: UserSettings)
	extends Refreshable[EditableLabel] with StackableAwtComponentWrapperWrapper
{
	// ATTRIBUTES	--------------------
	
	private implicit val originLanguage: String = Settings.sourceLanguageCode
	private implicit val localizer: Localizer = settings.localizer
	private implicit val baseContext: ComponentContext = context.withTextFieldWidth(
		context.normalWidth.upscaling.withLowPriority).result
	
	private var _label = initialLabel
	
	private val labelNameField = TextField.contextual(initialText = _label.name,
		prompt = Some[LocalizedString]("Name in %s").map { _.interpolate(settings.languages.head.localName) })
	private val typeSelection = DropDown.contextual[DataType]("Select Type", DisplayFunction.localized[DataType] {
		case StringType => "Text"
		case BooleanType => "Boolean"
		case d => d.toString
	}, DataType.values)
	private val isEmailSwitch = Switch.contextual
	private val isIdentifierSwitch = Switch.contextual
	
	private val _view = SegmentedRow.partOfGroupWithItems(segmentGroup, Vector(labelNameField, typeSelection,
		isEmailSwitch.alignedToCenter, isIdentifierSwitch.alignedToCenter),
		margin = baseContext.relatedItemsStackMargin.downscaling, layout = Fit)
	
	
	// INITIAL CODE	--------------------
	
	// typeSelection.addValueListener(d => println(s"Label: ${_label.label.id}, change: $d"))
	typeSelection.selectOne(_label.dataType)
	isEmailSwitch.isOn = _label.isEmail
	isIdentifierSwitch.isOn = _label.isIdentifier
	
	// Records changes in the fields
	labelNameField.addResultListener { name => _label.name = name.getOrElse("") }
	typeSelection.addValueListener { _.newValue.foreach { newType => _label.dataType = newType } }
	isEmailSwitch.addValueListener { c => _label.isEmail = c.newValue }
	isIdentifierSwitch.addValueListener { c => _label.isIdentifier = c.newValue }
	
	// Listens to undo / redo changes in label
	_label.addUndoListener(UndoHandler)
	
	
	// COMPUTED	------------------------
	
	/**
	 * @return View of this VC
	 */
	def view = _view
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrapped = _view
	
	override def content_=(newContent: EditableLabel) =
	{
		// Stops listening to changes in the old label
		_label.removeUndoListener(UndoHandler)
		
		// Updates local data & UI
		_label = newContent
		refreshUI()
		_label.addUndoListener(UndoHandler)
	}
	
	override def content = _label
	
	
	// OTHER	-------------------
	
	private def refreshUI(): Unit =
	{
		labelNameField.text = _label.name
		typeSelection.selectOne(_label.dataType)
		isEmailSwitch.isOn = _label.isEmail
		isIdentifierSwitch.isOn = _label.isIdentifier
	}
	
	
	// NESTED	-------------------
	
	private object UndoHandler extends UndoListener
	{
		override def onDataChanged() = refreshUI()
	}
}
