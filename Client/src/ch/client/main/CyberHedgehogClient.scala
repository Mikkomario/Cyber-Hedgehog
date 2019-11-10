package ch.client.main

import utopia.reflection.shape.LengthExtensions._
import ch.client.model.{ColorScheme, ColorSet, Margins, UserSettings}
import ch.client.view.EntityLabelsVC
import ch.database.{ConnectionPool, Language}
import ch.util.ThreadPool
import utopia.flow.generic.DataType
import utopia.genesis.color.RGB
import utopia.genesis.handling.ActorLoop
import utopia.genesis.handling.mutable.ActorHandler
import utopia.reflection.container.stack.StackHierarchyManager
import utopia.reflection.container.swing.window.Frame
import utopia.reflection.container.swing.window.WindowResizePolicy.User
import utopia.reflection.shape.StackLength
import utopia.reflection.text.Font
import utopia.reflection.util.{ComponentContext, ComponentContextBuilder}
import utopia.vault.util.{ErrorHandling, ErrorHandlingPrinciple}

import scala.concurrent.ExecutionContext

/**
 * The main app for cyber hedgehog client
 * @author Mikko Hilpinen
 * @since 25.10.2019, v3+
 */
object CyberHedgehogClient extends App
{
	DataType.setup()
	
	// Test version
	implicit val colorScheme: ColorScheme = ColorScheme(
		ColorSet(RGB.withValues(48, 63, 159), RGB.withValues(102, 106, 209), RGB.withValues(0, 25, 112)),
		ColorSet(RGB.withValues(170, 0, 255), RGB.withValues(226, 84, 255), RGB.withValues(114, 0, 202)),
		ColorSet(RGB.grayWithValue(225), RGB.grayWithValue(245), RGB.grayWithValue(164)))
	implicit val margins: Margins = Margins(16)
	
	ErrorHandling.defaultPrinciple = ErrorHandlingPrinciple.Throw
	// Connection.modifySettings(_.copy(debugPrintsEnabled = true))
	
	implicit val exc: ExecutionContext = ThreadPool.executionContext
	val actorHandler = ActorHandler()
	
	// Reads some settings from DB before starting
	val languages = ConnectionPool { implicit connection => Vector(Language.forCode("en"), Language.forCode("fi")).flatten }
	
	// Sets up settings
	implicit val settings: UserSettings = UserSettings(languages)
	implicit val baseContextBuilder: ComponentContextBuilder = ComponentContextBuilder(actorHandler, Font("Arial", 18),
		colorScheme.secondary, colorScheme.secondary.light, 320, insideMargins = margins.mini.any.square,
		borderWidth = Some(4), stackMargin = margins.medium.downscaling,
		relatedItemsStackMargin = Some(margins.small.downscaling), switchWidth = Some(StackLength(32, 48, 64)),
		scrollBarWidth = 16, scrollBarIsInsideContent = true)
	implicit val baseContext: ComponentContext = baseContextBuilder.result
	
	// Creates UI
	val content = new EntityLabelsVC(1)
	
	val actionLoop = new ActorLoop(actorHandler)
	val frame = Frame.windowed(content, "Cyber Hedgehog Client", User)
	frame.setToExitOnClose()
	
	actionLoop.registerToStopOnceJVMCloses()
	actionLoop.startAsync()
	StackHierarchyManager.startRevalidationLoop()
	frame.startEventGenerators(actorHandler)
	frame.isVisible = true
}
