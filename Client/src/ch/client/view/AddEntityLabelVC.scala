package ch.client.view

import ch.client.model.UserSettings
import ch.client.util.Settings
import utopia.reflection.localization.Localizer
import utopia.reflection.util.ComponentContext

/**
 * This view is used for adding new labels
 * @author Mikko Hilpinen
 * @since 3.11.2019, v3+
 */
class AddEntityLabelVC(implicit context: ComponentContext, settings: UserSettings)
{
	// ATTRIBUTES	------------------------
	
	private implicit val originLanguage: String = Settings.sourceLanguageCode
	private implicit val localizer: Localizer = settings.localizer
}
