package dev.ahmedabusada.atmos.feature.cities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.ahmedabusada.atmos.R
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.location.City
import dev.ahmedabusada.atmos.domain.location.Place
import dev.ahmedabusada.atmos.domain.settings.Temperature
import dev.ahmedabusada.atmos.domain.weather.WeatherCondition
import dev.ahmedabusada.atmos.ui.components.ErrorState
import dev.ahmedabusada.atmos.ui.components.MessageState
import dev.ahmedabusada.atmos.ui.components.WeatherIcon
import dev.ahmedabusada.atmos.ui.format.rememberWeatherFormatter
import dev.ahmedabusada.atmos.ui.preview.AtmosPreviews
import dev.ahmedabusada.atmos.ui.preview.PreviewData
import dev.ahmedabusada.atmos.ui.theme.AtmosTheme
import dev.ahmedabusada.atmos.ui.weather.iconRes
import dev.ahmedabusada.atmos.ui.weather.labelRes

/**
 * The Cities screen (US-04, US-05). The snackbar host is passed in, because *when* to show
 * "Ankara removed · Undo" is decided by the ViewModel (step 06), not by the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitiesScreen(
    state: CitiesUiState,
    onEvent: (CitiesUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cities_title)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(CitiesUiEvent.Back) }) {
                        // autoMirrored in the drawable: the arrow points right in Arabic.
                        Icon(painterResource(R.drawable.ic_arrow_back), stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            SearchField(state.query, onEvent)
            when (val search = state.search) {
                SearchState.Idle -> SavedPlaces(state, onEvent)

                SearchState.Searching -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                is SearchState.Results -> SearchResults(search.cities, onEvent)

                SearchState.NoResults -> MessageState(stringResource(R.string.cities_no_results))

                is SearchState.Failed -> ErrorState(search.error, onRetry = { onEvent(CitiesUiEvent.RetrySearch) })
            }
        }
    }
}

@Composable
private fun SearchField(query: String, onEvent: (CitiesUiEvent) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = { onEvent(CitiesUiEvent.QueryChanged(it)) },
        placeholder = { Text(stringResource(R.string.cities_search_hint)) },
        leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onEvent(CitiesUiEvent.ClearQuery) }) {
                    Icon(painterResource(R.drawable.ic_close), stringResource(R.string.cities_clear_search))
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        modifier = Modifier.fillMaxWidth().padding(horizontal = AtmosTheme.spacing.medium),
    )
}

@Composable
private fun SearchResults(cities: List<City>, onEvent: (CitiesUiEvent) -> Unit) {
    LazyColumn {
        items(cities, key = { it.id }) { city ->
            ListItem(
                headlineContent = { Text(city.name) },
                supportingContent = { Text(listOfNotNull(city.region, city.country).joinToString(" · ")) },
                trailingContent = {
                    IconButton(onClick = { onEvent(CitiesUiEvent.Add(city)) }) {
                        Icon(painterResource(R.drawable.ic_add), stringResource(R.string.cities_add, city.name))
                    }
                },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun SavedPlaces(state: CitiesUiState, onEvent: (CitiesUiEvent) -> Unit) {
    LazyColumn {
        items(state.places, key = { it.place.key() }) { item ->
            when (val place = item.place) {
                // The current location is always first and can't be removed (US-05).
                Place.CurrentLocation -> PlaceRow(item, state, onEvent)

                is Place.Saved -> SwipeToRemove(onRemove = { onEvent(CitiesUiEvent.Remove(place.city)) }) {
                    PlaceRow(item, state, onEvent)
                }
            }
            HorizontalDivider()
        }
        if (state.places.none { it.place is Place.Saved }) {
            item { MessageState(stringResource(R.string.cities_empty_saved)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToRemove(onRemove: () -> Unit, content: @Composable () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState()
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Row(
                Modifier.fillMaxSize().padding(horizontal = AtmosTheme.spacing.large),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(painterResource(R.drawable.ic_delete), contentDescription = null)
            }
        },
        enableDismissFromStartToEnd = false,
        onDismiss = { value -> if (value == SwipeToDismissBoxValue.EndToStart) onRemove() },
    ) { content() }
}

@Composable
private fun PlaceRow(item: PlaceItem, state: CitiesUiState, onEvent: (CitiesUiEvent) -> Unit) {
    val format = rememberWeatherFormatter()
    val name = when (val place = item.place) {
        Place.CurrentLocation -> stringResource(R.string.home_current_location)
        is Place.Saved -> place.city.name
    }
    ListItem(
        headlineContent = { Text(name) },
        leadingContent = if (item.place == Place.CurrentLocation) {
            { Icon(painterResource(R.drawable.ic_location), contentDescription = null) }
        } else {
            null
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                item.temperature?.let {
                    Text(format.temperature(it, state.temperatureUnit), style = MaterialTheme.typography.titleMedium)
                }
                item.condition?.let { WeatherIcon(it.iconRes(), stringResource(it.labelRes())) }
                // A visible action as well as the swipe: swipe gestures are not discoverable
                // and not usable with every assistive technology (NF-03).
                (item.place as? Place.Saved)?.let { saved ->
                    IconButton(onClick = { onEvent(CitiesUiEvent.Remove(saved.city)) }) {
                        Icon(painterResource(R.drawable.ic_delete), stringResource(R.string.cities_remove, name))
                    }
                }
            }
        },
        modifier = Modifier.clickable { onEvent(CitiesUiEvent.Select(item.place)) },
    )
}

private fun Place.key(): String = when (this) {
    Place.CurrentLocation -> "current"
    is Place.Saved -> "city-${city.id}"
}

internal class CitiesStateProvider : PreviewParameterProvider<CitiesUiState> {
    private val places = listOf(
        PlaceItem(Place.CurrentLocation, Temperature(14.9), WeatherCondition.Overcast),
        PlaceItem(Place.Saved(PreviewData.ankara), Temperature(12.0), WeatherCondition.Rain),
        PlaceItem(Place.Saved(PreviewData.london), Temperature(9.0), WeatherCondition.Fog),
    )
    override val values = sequenceOf(
        CitiesUiState(places = places),
        CitiesUiState(query = "ista", search = SearchState.Results(listOf(PreviewData.istanbul)), places = places),
        CitiesUiState(query = "zzqqxx", search = SearchState.NoResults, places = places),
        CitiesUiState(query = "ista", search = SearchState.Failed(DomainError.NoConnection), places = places),
        CitiesUiState(places = places.take(1)),
    )
}

@AtmosPreviews
@Composable
private fun CitiesScreenPreview(@PreviewParameter(CitiesStateProvider::class) state: CitiesUiState) {
    AtmosTheme(dynamicColor = false) { CitiesScreen(state = state, onEvent = {}) }
}
