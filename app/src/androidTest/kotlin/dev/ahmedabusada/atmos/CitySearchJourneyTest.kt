package dev.ahmedabusada.atmos

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End to end on a real device or emulator, against the **real** Open-Meteo API: no fakes.
 *
 * The journey for a user who has not given location permission (F-02, US-04, US-05):
 * Home explains → search → add → choose → the forecast is shown. If any layer is broken
 * (Hilt wiring, Retrofit, JSON, Room, navigation), this test fails.
 *
 * Runs in CI on an emulator (`./gradlew connectedDebugAndroidTest`). The device language
 * must be English, the emulator default.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class CitySearchJourneyTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private val network = 20_000L

    @Test
    fun withoutLocation_searchAddAndChooseACity_showsItsForecast() {
        // Home: no permission yet, so the explanation is shown instead of an error.
        compose.waitUntilAtLeastOneExists(hasText("Where are you?"), network)
        compose.onNode(hasText("Search for a city")).performClick()

        // Cities: real search, debounced.
        compose.onNode(hasSetTextAction()).performTextInput("Istanbul")
        compose.waitUntilAtLeastOneExists(hasContentDescription("Add Istanbul"), network)
        compose.onAllNodes(hasContentDescription("Add Istanbul")).onFirst().performClick()

        // Saved list: choose the city, which goes back to Home.
        compose.waitUntilAtLeastOneExists(hasText("Istanbul"), network)
        compose.onAllNodes(hasText("Istanbul")).onFirst().performClick()

        // Home: the real forecast for Istanbul.
        compose.waitUntilAtLeastOneExists(hasText("Next 24 hours"), network)
        compose.waitUntilAtLeastOneExists(hasText("Istanbul"), network)
    }
}
