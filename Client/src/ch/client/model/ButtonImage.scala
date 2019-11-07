package ch.client.model

import utopia.genesis.color.Color
import utopia.genesis.image.Image
import utopia.reflection.component.swing.button.ButtonImageSet

/**
 * This model holds a couple of image configurations for buttons
 * @author Mikko Hilpinen
 * @since 7.11.2019, v3+
 */
class ButtonImage(private val originalImage: Image)
{
	/**
	 * Version of this image to be used in light context
	 */
	val dark = ButtonImageSet.varyingAlpha(originalImage, 0.65, 0.65)
	/**
	 * Version of this image to be used in dark context
	 */
	lazy val white = ButtonImageSet.varyingAlpha(originalImage.withColorOverlay(Color.white), 1, 1)
}
