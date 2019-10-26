package ch.client.view

import ch.client.model.UserSettings
import ch.client.util.Settings
import ch.model.DataType.{BooleanType, StringType}
import ch.model.{DataType, EntityLabel, EntityLabelDescription}
import utopia.reflection.component.Refreshable
import utopia.reflection.component.swing.{DropDown, StackableAwtComponentWrapperWrapper, Switch, TextField}
import utopia.reflection.container.swing.Stack
import utopia.reflection.localization.{DisplayFunction, LocalizedString, Localizer}
import utopia.reflection.util.ComponentContext

/**
 * Shows editable data for an entity label
 * @author Mikko Hilpinen
 * @since 25.10.2019, v3+
 */
class EntityLabelRowVC(initialLabel: EntityLabel, initialDescriptions: Vector[EntityLabelDescription])
					  (implicit context: ComponentContext, settings: UserSettings)
	extends Refreshable[(EntityLabel, Vector[EntityLabelDescription])] with StackableAwtComponentWrapperWrapper
{
	// ATTRIBUTES	--------------------
	
	implicit val originLanguage: String = Settings.sourceLanguageCode
	implicit val localizer: Localizer = settings.localizer
	
	private var _label = initialLabel
	private var _descriptions = initialDescriptions
	
	private val labelNameField = TextField.contextual(initialText = initialDescriptions.headOption.map { _.name }.getOrElse(""),
		prompt = Some[LocalizedString]("Name in %s").map { _.interpolate(settings.languages.head.localName) })
	private val typeSelection = DropDown.contextual[DataType]("Select Type", DisplayFunction.localized {
		case StringType => if (_label.isEmail) "Email" else "Text" // TODO: Email can't be configured like this if using data type selection
		case BooleanType => "Boolean"
		case d => d.toString
	}, DataType.values)
	private val isIdentifierSwitch = Switch.contextual
	
	private val _view = Stack.buildRowWithContext() { s =>
		s += labelNameField
		s += typeSelection
		s += isIdentifierSwitch
	}
	
	
	// INITIAL CODE	--------------------
	
	isIdentifierSwitch.isOn = _label.isIdentifier
	
	
	// IMPLEMENTED	--------------------
	
	override protected def wrapped = _view
	
	override def content_=(newContent: (EntityLabel, Vector[EntityLabelDescription])) =
	{
		_label = newContent._1
		_descriptions = newContent._2
	}
	
	override def content = _label -> _descriptions
}
