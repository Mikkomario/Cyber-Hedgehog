package ch.client.view

import utopia.reflection.shape.LengthExtensions._
import ch.client.model.UserSettings
import ch.client.util.Settings
import ch.database.{Entities, Entity}
import ch.model.DescribedEntityLabel
import utopia.genesis.shape.Axis.X
import utopia.reflection.component.swing.StackableAwtComponentWrapperWrapper
import utopia.reflection.component.swing.label.TextLabel
import utopia.reflection.container.stack.segmented.SegmentedGroup
import utopia.reflection.container.swing.{AwtContainerRelated, ScrollView, SegmentedRow, Stack}
import utopia.reflection.controller.data.StackContentManager
import utopia.reflection.localization.Localizer
import utopia.reflection.util.ComponentContext
import utopia.vault.database.Connection

/**
 * Shows and handles editing of all entity labels
 * @author Mikko Hilpinen
 * @since 30.10.2019, v3+
 */
class EntityLabelsVC(val entityTypeId: Int)(implicit context: ComponentContext, settings: UserSettings,
											connection: Connection)
	extends StackableAwtComponentWrapperWrapper with AwtContainerRelated
{
	// ATTRIBUTES	-------------------------------
	
	private implicit val originLanguage: String = Settings.sourceLanguageCode
	private implicit val localizer: Localizer = settings.localizer
	
	private val segmentGroup = new SegmentedGroup(X)
	private val header = SegmentedRow.partOfGroupWithItems(segmentGroup, Vector("Field Name", "Field Type", "Email",
		"Identifier").map { text => TextLabel.contextual(text) })
	private val rowStack = Stack.column[EntityLabelRowVC]()
	
	private val _view = Stack.buildColumn(margin = 0.fixed) { s =>
		s += header
		s += ScrollView.contextual(rowStack)
		// TODO: Add new label -area
	}
	
	private val contentManager = new StackContentManager[DescribedEntityLabel, EntityLabelRowVC](rowStack,
		l => new EntityLabelRowVC(segmentGroup, l))
	
	
	// INITIAL CODE	-------------------------------
	
	// Reads label data from DB and displays it
	contentManager.content = Entities.labels.forTypeWithId(entityTypeId).get.map {
		l => DescribedEntityLabel(l, Entity.label(l.id).descriptions(settings.languages.map { _.id })) }
	
	
	// IMPLEMENTED	-------------------------------
	
	override protected def wrapped = _view
	
	override def component = _view.component
}
