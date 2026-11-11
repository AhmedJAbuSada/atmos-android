package dev.ahmedabusada.atmos.core

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dev.ahmedabusada.atmos.domain.settings.AppLanguage
import java.util.Locale
import javax.inject.Inject

/** The language the app is shown in right now, as a tag for the geocoding API. */
fun interface CurrentLanguage {
    fun tag(): String
}

/** Reads and changes the app language. An interface, so ViewModels are tested without Android. */
interface AppLanguageController {
    fun current(): AppLanguage

    fun apply(language: AppLanguage)
}

/**
 * The app language lives in **one** place: the platform's per-app locale
 * (`AppCompatDelegate.setApplicationLocales`, ADR-0005). On Android 13+ the user can also
 * change it in the system settings; storing our own copy elsewhere and re-applying it at
 * start-up would silently undo that choice. So we read it from, and write it to, the
 * platform only.
 */
class AppLanguages @Inject constructor() :
    CurrentLanguage,
    AppLanguageController {
    override fun current(): AppLanguage {
        val tag = AppCompatDelegate.getApplicationLocales()[0]?.language ?: return AppLanguage.System
        return AppLanguage.entries.firstOrNull { it.tag == tag } ?: AppLanguage.System
    }

    override fun apply(language: AppLanguage) {
        val locales = language.tag?.let(LocaleListCompat::forLanguageTags) ?: LocaleListCompat.getEmptyLocaleList()
        // Only when it changes: setting the same locales again would recreate the activity.
        if (AppCompatDelegate.getApplicationLocales() != locales) AppCompatDelegate.setApplicationLocales(locales)
    }

    /** The chosen app language, else the first device language Atmos supports, else English. */
    override fun tag(): String {
        val device = LocaleListCompat.getAdjustedDefault().let { list -> (0 until list.size()).mapNotNull { list[it] } }
        return (listOfNotNull(current().tag) + device.map(Locale::getLanguage))
            .firstOrNull { it in SUPPORTED } ?: DEFAULT_TAG
    }

    private companion object {
        val SUPPORTED = AppLanguage.entries.mapNotNull { it.tag }.toSet()
        const val DEFAULT_TAG = "en"
    }
}
