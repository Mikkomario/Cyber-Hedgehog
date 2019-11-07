package ch.client.view

import ch.client.controller.Images
import ch.client.model.UserSettings
import ch.client.util.Settings
import ch.model.DataType
import ch.model.DataType.{BooleanType, StringType}
import utopia.genesis.shape.Axis.X
import utopia.genesis.shape.shape2D.Direction2D
import utopia.reflection.component.swing.button.ImageAndTextButton
import utopia.reflection.component.swing.label.TextLabel
import utopia.reflection.component.swing.{DropDown, StackableAwtComponentWrapperWrapper, Switch, TextField}
import utopia.reflection.container.stack.segmented.SegmentedGroup
import utopia.reflection.container.swing.{SegmentedRow, Stack}
import utopia.reflection.localization.{DisplayFunction, LocalizedString, Localizer}
import utopia.reflection.util.ComponentContext

/**
 * This view is used for adding new labels
 * @author Mikko Hilpinen
 * @since 3.11.2019, v3+
 */
class AddEntityLabelVC(implicit context: ComponentContext, settings: UserSettings) extends StackableAwtComponentWrapperWrapper
{
	// ATTRIBUTES	------------------------
	
	private implicit val originLanguage: String = Settings.sourceLanguageCode
	private implicit val localizer: Localizer = settings.localizer
	
	// TODO: Contains a lot of repetition from row vc
	private val nameField = TextField.contextual(prompt = Some[LocalizedString]("Name in %s")
		.map { _.interpolate(settings.languages.head.localName) })
	private val typeSelection = DropDown.contextual[DataType]("Select Type", DisplayFunction.localized[DataType] {
		case StringType => "Text"
		case BooleanType => "Boolean"
		case d => d.toString
	}, DataType.values)
	private val isEmailSwitch = Switch.contextual
	private val isIdentifierSwitch = Switch.contextual
	
	private val addButton = ImageAndTextButton.contextual(Images.forButtons.addBox.white, "Add") {
		() => println(s"Adding new label: ${nameField.value}") }
	
	private val _view =
	{
		Stack.buildRowWithContext() { contentRow =>
			
			// Main input with fields and their headers
			contentRow += Stack.buildColumnWithContext(isRelated = true) { inputColumn =>
				val group = new SegmentedGroup(X)
				inputColumn += SegmentedRow.partOfGroupWithItems(group,
					Vector[LocalizedString]("Field Name", "Type", "Email", "ID").map { TextLabel.contextual(_) })
				inputColumn += SegmentedRow.partOfGroupWithItems(group, Vector(nameField, typeSelection, isEmailSwitch, isIdentifierSwitch))
			}
			
			// Followed by a "add" button
			contentRow += addButton
			
		}.alignedToSide(Direction2D.Right)//.framed() TODO: Frame this vc
	}
	
	
	// COMPUTED	-------------------------
	
	/**
	 * @return View portion of this VC
	 */
	def view = _view
	
	
	// IMPLEMENTED	---------------------
	
	override protected def wrapped = _view
}
