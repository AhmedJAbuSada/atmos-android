package dev.ahmedabusada.atmos

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dev.ahmedabusada.atmos.domain.settings.AppTheme
import dev.ahmedabusada.atmos.navigation.AtmosNavHost
import dev.ahmedabusada.atmos.ui.theme.AtmosTheme

/**
 * Single activity: applies the theme setting and hosts the navigation graph.
 *
 * It is an `AppCompatActivity` only for the per-app language API on Android 8–12
 * (ADR-0005); on 13+ the system handles it. The language is applied by Settings, never
 * here: re-applying it at start-up would undo a choice made in the system settings.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            AtmosTheme(appTheme = settings?.theme ?: AppTheme.System) {
                AtmosNavHost()
            }
        }
    }
}
