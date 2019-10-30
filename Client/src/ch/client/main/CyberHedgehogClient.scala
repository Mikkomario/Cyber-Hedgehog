package ch.client.main

import ch.client.model.UserSettings
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
import utopia.reflection.text.Font
import utopia.reflection.util.{ComponentContext, ComponentContextBuilder}
import utopia.vault.database.Connection
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
	val primaryColor = RGB.withValues(48, 63, 159)
	val secondaryColor = RGB.withValues(170, 0, 255)
	
	ErrorHandling.defaultPrinciple = ErrorHandlingPrinciple.Throw
	// Connection.modifySettings(_.copy(debugPrintsEnabled = true))
	
	implicit val exc: ExecutionContext = ThreadPool.executionContext
	val actorHandler = ActorHandler()
	ConnectionPool { implicit connection =>
		
		// Reads language data from DB
		val languages = Vector(Language.forCode("en"), Language.forCode("fi")).flatten
		
		// Sets up settings
		implicit val settings: UserSettings = UserSettings(languages)
		implicit val baseContext: ComponentContext = ComponentContextBuilder(actorHandler, Font("Arial", 16), secondaryColor,
			secondaryColor, 320).result
		
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
}
