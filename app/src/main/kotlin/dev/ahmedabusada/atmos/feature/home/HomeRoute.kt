package dev.ahmedabusada.atmos.feature.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Connects [HomeScreen] to [HomeViewModel] and to the Android things a ViewModel must not
 * touch: the permission dialog and navigation. The screen stays stateless and previewable.
 *
 * Permission flow (US-01, F-02): the NeedsLocation state *is* the explanation shown before
 * the system dialog. If the user has denied it twice, Android stops showing the dialog,
 * so the button opens the app's system settings instead.
 */
@Composable
fun HomeRoute(onOpenCities: () -> Unit, onOpenSettings: () -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var denials by rememberSaveable { mutableIntStateOf(0) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) denials++
        viewModel.onLocationPermissionResult(granted)
    }

    HomeScreen(
        state = state,
        onEvent = { event ->
            when (event) {
                HomeUiEvent.OpenCities -> onOpenCities()

                HomeUiEvent.OpenSettings -> onOpenSettings()

                HomeUiEvent.RequestLocation ->
                    if (denials < MAX_DIALOG_DENIALS) {
                        permission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    } else {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null),
                            ),
                        )
                    }

                else -> viewModel.onEvent(event)
            }
        },
    )
}

/** Android shows the permission dialog at most twice before it answers "denied" by itself. */
private const val MAX_DIALOG_DENIALS = 2
