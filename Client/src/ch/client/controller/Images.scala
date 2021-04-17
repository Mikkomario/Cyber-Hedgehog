package ch.client.controller

import java.nio.file.Paths

import ch.client.model.ButtonImage
import ch.util.Log
import utopia.flow.caching.multi.ReleasingCache
import utopia.flow.time.TimeExtensions._
import utopia.genesis.image.Image
import utopia.genesis.shape.shape2D.Size

import scala.util.{Failure, Success}

/**
 * Caches and provides access to various images
 * @author Mikko Hilpinen
 * @since 7.11.2019, v3+
 */
object Images
{
	// ATTRIBUTES	-----------------------
	
	private val mainCache = ReleasingCache[String, Image](3.minutes) { k => Image.readFrom(Paths.get(s"resources/images/$k")) match
	{
		case Success(img) => img
		case Failure(exception) =>
			Log(exception, s"Failed to load image: images/$k")
			Image.empty
	}}
	
	
	// COMPUTED	---------------------------
	
	/**
	 * @return Access point to button-specific images
	 */
	def forButtons = ButtonIcons
	
	
	// OTHER	---------------------------
	
	/**
	 * @param imageFileName File name of targeted image
	 * @return Image read from file (or cache)
	 */
	def apply(imageFileName: String) = mainCache(imageFileName)
	
	
	// NESTED	---------------------------
	
	object ButtonIcons
	{
		// ATTRIBUTES	-------------------
		
		private val cache = ReleasingCache[String, ButtonImage](5.minutes) {
			k => new ButtonImage(Images(k).withSize(Size(48, 48))) }
		
		
		// COMPUTED	----------------------
		
		/**
		 * @return Add button icon
		 */
		def addBox = apply("add-box.png")
		
		
		// OTHER	----------------------
		
		/**
		 * @param imageFileName File name of targeted image
		 * @return Image for button context (use either dark or light)
		 */
		def apply(imageFileName: String) = cache(imageFileName)
		
		/**
		 * @param fileName File name of targeted image
		 * @return White button image set for dark context
		 */
		def light(fileName: String) = apply(fileName).white
		
		/**
		 * @param fileName file name of targeted image
		 * @return Black button image set for light context
		 */
		def dark(fileName: String) = apply(fileName).dark
	}
}
