package dev.ahmedabusada.atmos.feature.cities

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ahmedabusada.atmos.R
import kotlinx.coroutines.launch

/** Connects [CitiesScreen] to [CitiesViewModel]; turns effects into snackbars and navigation. */
@Composable
fun CitiesRoute(onClose: () -> Unit, viewModel: CitiesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val removed = stringResource(R.string.cities_removed, "%s")
    val alreadySaved = stringResource(R.string.cities_already_saved, "%s")
    val undo = stringResource(R.string.cities_undo)

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                CitiesEffect.Close -> onClose()

                is CitiesEffect.ShowAlreadySaved -> launch {
                    snackbar.showSnackbar(alreadySaved.format(effect.city.name))
                }

                // Undo for 5 seconds (US-05): a short snackbar lasts about 4–10 s depending
                // on accessibility settings, so "Long" plus an action is the safe choice.
                is CitiesEffect.ShowRemoved -> launch {
                    val result = snackbar.showSnackbar(
                        message = removed.format(effect.city.name),
                        actionLabel = undo,
                        duration = SnackbarDuration.Long,
                    )
                    if (result == SnackbarResult.ActionPerformed) viewModel.undoRemove(effect.city)
                }
            }
        }
    }

    CitiesScreen(state = state, onEvent = viewModel::onEvent, snackbarHostState = snackbar)
}
