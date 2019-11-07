package ch.client.model

/**
 * Specifies colors used in the client
 * @author Mikko Hilpinen
 * @since 7.11.2019, v3+
 * @param primary Primary color theme, used for most components
 * @param secondary Secondary color theme, used for highlighted components
 */
case class ColorScheme(primary: ColorSet, secondary: ColorSet, gray: ColorSet)
