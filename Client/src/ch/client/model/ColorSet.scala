package ch.client.model

import scala.language.implicitConversions
import utopia.genesis.color.Color

object ColorSet
{
	/**
	 * Converts a color set into its default color implicitly
	 * @param set A color set
	 * @return Set as a color
	 */
	implicit def setToColor(set: ColorSet): Color = set.default
}

/**
 * Specifies a color for various contexts
 * @author Mikko Hilpinen
 * @since 7.11.2019, v3+
 * @param default The most commonly used (standard) version of this color
 * @param light Light variation of this color
 * @param dark Dark variation of this color
 */
case class ColorSet(default: Color, light: Color, dark: Color)
