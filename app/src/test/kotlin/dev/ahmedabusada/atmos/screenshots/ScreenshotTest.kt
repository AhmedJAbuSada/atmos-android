package dev.ahmedabusada.atmos.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.location.Place
import dev.ahmedabusada.atmos.domain.settings.AppLanguage
import dev.ahmedabusada.atmos.domain.settings.AppTheme
import dev.ahmedabusada.atmos.domain.settings.Temperature
import dev.ahmedabusada.atmos.domain.settings.UserSettings
import dev.ahmedabusada.atmos.domain.weather.WeatherCondition
import dev.ahmedabusada.atmos.feature.cities.CitiesScreen
import dev.ahmedabusada.atmos.feature.cities.CitiesUiState
import dev.ahmedabusada.atmos.feature.cities.PlaceItem
import dev.ahmedabusada.atmos.feature.cities.SearchState
import dev.ahmedabusada.atmos.feature.home.HomeScreen
import dev.ahmedabusada.atmos.feature.home.HomeUiState
import dev.ahmedabusada.atmos.feature.settings.SettingsScreen
import dev.ahmedabusada.atmos.ui.preview.PreviewData
import dev.ahmedabusada.atmos.ui.theme.AtmosTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.Duration

/**
 * Renders the real screens on the JVM (Robolectric native graphics) and saves them as the
 * images in docs/screenshots, used by the README and the step guides.
 *
 * In a normal test run this still renders every screen, so a crash in any state fails the
 * build; images are written only with `-PrecordScreenshots`. A next step would be to
 * *compare* against the saved images and fail on unexpected changes (Roborazzi's verify
 * mode).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = "w411dp-h891dp-xxhdpi")
class ScreenshotTest {
    @get:Rule
    val compose = createComposeRule()

    private val dir = System.getProperty("atmos.screenshots.dir") ?: "build/screenshots"

    private val content = HomeUiState.Content(
        placeName = PreviewData.istanbul.name,
        forecast = PreviewData.forecast,
        settings = UserSettings(),
    )

    private fun capture(
        name: String,
        qualifiers: String = "",
        theme: AppTheme = AppTheme.Light,
        ui: @Composable () -> Unit,
    ) {
        if (qualifiers.isNotEmpty()) RuntimeEnvironment.setQualifiers("+$qualifiers")
        compose.setContent { AtmosTheme(appTheme = theme, dynamicColor = false, content = ui) }
        compose.onRoot().captureRoboImage("$dir/$name.png")
    }

    @Test
    fun homeLight() = capture("home_light") { HomeScreen(content, onEvent = {}) }

    @Test
    fun homeDark() = capture("home_dark", "night", AppTheme.Dark) { HomeScreen(content, onEvent = {}) }

    @Test
    fun homeArabic() = capture("home_arabic", "ar") { HomeScreen(content, onEvent = {}) }

    @Test
    fun homeTurkishOutdated() = capture("home_turkish_outdated", "tr") {
        HomeScreen(content.copy(outdatedBy = Duration.ofHours(2)), onEvent = {})
    }

    @Test
    fun homeNeedsLocation() = capture("home_needs_location") { HomeScreen(HomeUiState.NeedsLocation, onEvent = {}) }

    @Test
    fun homeError() = capture("home_error") { HomeScreen(HomeUiState.Error(DomainError.NoConnection), onEvent = {}) }

    @Test
    fun citiesSearch() = capture("cities_search") {
        CitiesScreen(
            CitiesUiState(query = "ista", search = SearchState.Results(listOf(PreviewData.istanbul))),
            onEvent = {},
        )
    }

    @Test
    fun citiesSaved() = capture("cities_saved") {
        CitiesScreen(
            CitiesUiState(
                places = listOf(
                    PlaceItem(Place.CurrentLocation),
                    PlaceItem(Place.Saved(PreviewData.ankara), Temperature(12.0), WeatherCondition.Rain),
                    PlaceItem(Place.Saved(PreviewData.london), Temperature(9.0), WeatherCondition.Fog),
                ),
            ),
            onEvent = {},
        )
    }

    @Test
    fun settingsArabicDark() = capture("settings_arabic_dark", "ar-night", AppTheme.Dark) {
        SettingsScreen(UserSettings(language = AppLanguage.Arabic, theme = AppTheme.Dark), onEvent = {})
    }
}
