package dev.ahmedabusada.atmos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import dev.ahmedabusada.atmos.domain.settings.UserSettings
import dev.ahmedabusada.atmos.feature.home.HomeScreen
import dev.ahmedabusada.atmos.feature.home.HomeUiState
import dev.ahmedabusada.atmos.ui.preview.PreviewData
import dev.ahmedabusada.atmos.ui.theme.AtmosTheme

/**
 * Single activity. In step 05 it shows the Home screen with **preview data**, so the
 * views can be checked on a device before any real data is connected. Step 06 replaces
 * this with navigation and ViewModels.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtmosTheme {
                HomeScreen(
                    state = HomeUiState.Content(
                        placeName = PreviewData.istanbul.name,
                        forecast = PreviewData.forecast,
                        settings = UserSettings(),
                    ),
                    onEvent = {},
                )
            }
        }
    }
}
