package ch.client.view

import ch.client.model.UserSettings
import ch.client.util.Settings
import ch.model.DataType.{BooleanType, StringType}
import ch.model.{DataType, DescribedEntityLabel}
import utopia.reflection.component.Refreshable
import utopia.reflection.component.swing.{DropDown, StackableAwtComponentWrapperWrapper, Switch, TextField}
import utopia.reflection.container.stack.StackLayout.Center
import utopia.reflection.container.stack.segmented.SegmentedGroup
import utopia.reflection.container.swing.SegmentedRow
import utopia.reflection.localization.{DisplayFunction, LocalizedString, Localizer}
import utopia.reflection.util.ComponentContext

/**
 * Shows editable data for an entity label
 * @author Mikko Hilpinen
 * @since 25.10.2019, v3+
 */
class EntityLabelRowVC(val segmentGroup: SegmentedGroup, initialLabel: DescribedEntityLabel)
					  (implicit context: ComponentContext, settings: UserSettings)
	extends Refreshable[DescribedEntityLabel] with StackableAwtComponentWrapperWrapper
{
	// ATTRIBUTES	--------------------
	
	private implicit val originLanguage: String = Settings.sourceLanguageCode
	private implicit val localizer: Localizer = settings.localizer
	
	private var _label = initialLabel
	
	private val labelNameField = TextField.contextual(initialText = _label.name.getOrElse(""),
		prompt = Some[LocalizedString]("Name in %s").map { _.interpolate(settings.languages.head.localName) })
	private val typeSelection = DropDown.contextual[DataType]("Select Type", DisplayFunction.localized[DataType] {
		case StringType => "Text"
		case BooleanType => "Boolean"
		case d => d.toString
	}, DataType.values)
	private val isEmailSwitch = Switch.contextual
	private val isIdentifierSwitch = Switch.contextual
	
	private val _view = SegmentedRow.partOfGroupWithItems(segmentGroup, Vector(labelNameField, typeSelection,
		isEmailSwitch, isIdentifierSwitch), layout = Center)
	
	
	// INITIAL CODE	--------------------
	
	typeSelection.addValueListener(d => println(s"Label: ${_label.label.id}, change: $d"))
	typeSelection.selectOne(_label.dataType)
	isEmailSwitch.isOn = _label.isEmail
	isIdentifierSwitch.isOn = _label.isIdentifier
	
	
	// COMPUTED	------------------------
	
	/**
	 * @return View of this VC
	 */
	def view = _view
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrapped = _view
	
	override def content_=(newContent: DescribedEntityLabel) =
	{
		println("updating content")
		
		// Updates local data
		_label = newContent
		
		// Updates UI
		labelNameField.text = _label.name.getOrElse("")
		typeSelection.selectOne(_label.dataType)
		isEmailSwitch.isOn = _label.isEmail
		isIdentifierSwitch.isOn = _label.isIdentifier
	}
	
	override def content = _label
}
