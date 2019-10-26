package ch.client.model

import ch.model.Language
import utopia.reflection.localization.{Localizer, NoLocalization}

/**
 * Contains settings selected by the user
 * @author Mikko Hilpinen
 * @since 25.10.2019, v3+
 * @param languages The languages known by user, from most to least preferred
 * @param localizer Localizer preferred by the user
 */
case class UserSettings(languages: Vector[Language], localizer: Localizer = NoLocalization)
