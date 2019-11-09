package ch.client.view

import utopia.reflection.shape.LengthExtensions._
import ch.client.model.{ColorScheme, EditableLabel, Margins, UserSettings}
import ch.client.util.Settings
import ch.database.{Entities, Entity}
import ch.model.DescribedEntityLabel
import utopia.genesis.color.Color
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

/**
 * Shows and handles editing of all entity labels
 * @author Mikko Hilpinen
 * @since 30.10.2019, v3+
 */
class EntityLabelsVC(val entityTypeId: Int)(implicit baseContextBuilder: ComponentContextBuilder, settings: UserSettings,
											margins: Margins, colorScheme: ColorScheme, connection: Connection)
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
	
	
	// INITIAL CODE	-------------------------------
	
	// Reads label data from DB and displays it in editable format
	contentManager.content = Entities.labels.forTypeWithId(entityTypeId).get.map {
		l => DescribedEntityLabel(l, Entity.label(l.id).descriptions(settings.languages.map { _.id })) }.map { new EditableLabel(_) }
	
	
	// IMPLEMENTED	-------------------------------
	
	override protected def wrapped = _view
	
	override def component = _view.component
}
