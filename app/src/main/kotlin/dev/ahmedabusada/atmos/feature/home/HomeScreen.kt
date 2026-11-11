package dev.ahmedabusada.atmos.feature.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.ahmedabusada.atmos.R
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.domain.settings.UserSettings
import dev.ahmedabusada.atmos.domain.weather.CurrentWeather
import dev.ahmedabusada.atmos.domain.weather.DailyForecast
import dev.ahmedabusada.atmos.domain.weather.HourlyForecast
import dev.ahmedabusada.atmos.ui.components.ErrorState
import dev.ahmedabusada.atmos.ui.components.InfoBanner
import dev.ahmedabusada.atmos.ui.components.MessageState
import dev.ahmedabusada.atmos.ui.components.SkeletonBlock
import dev.ahmedabusada.atmos.ui.components.WeatherIcon
import dev.ahmedabusada.atmos.ui.format.WeatherFormatter
import dev.ahmedabusada.atmos.ui.format.rememberWeatherFormatter
import dev.ahmedabusada.atmos.ui.preview.AtmosPreviews
import dev.ahmedabusada.atmos.ui.preview.PreviewData
import dev.ahmedabusada.atmos.ui.theme.AtmosTheme
import dev.ahmedabusada.atmos.ui.theme.TabularNumbers
import dev.ahmedabusada.atmos.ui.weather.iconRes
import dev.ahmedabusada.atmos.ui.weather.labelRes
import java.time.Duration

/**
 * The Home screen (US-01, US-02, US-03, US-07). Stateless: it receives [state] and reports
 * [onEvent]. It never talks to a ViewModel, a repository or the network.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(state: HomeUiState, onEvent: (HomeUiEvent) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val title = (state as? HomeUiState.Content)?.let {
                        it.placeName ?: stringResource(R.string.home_current_location)
                    }.orEmpty()
                    Text(title, modifier = Modifier.semantics { heading() })
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(HomeUiEvent.OpenCities) }) {
                        Icon(painterResource(R.drawable.ic_list), stringResource(R.string.home_open_cities))
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(HomeUiEvent.OpenSettings) }) {
                        Icon(painterResource(R.drawable.ic_settings), stringResource(R.string.home_open_settings))
                    }
                },
            )
        },
    ) { padding ->
        val content = Modifier.padding(padding)
        when (state) {
            HomeUiState.Loading -> HomeSkeleton(content)

            HomeUiState.NeedsLocation -> MessageState(
                title = stringResource(R.string.home_no_location_title),
                message = stringResource(R.string.home_no_location_message),
                actionLabel = stringResource(R.string.home_allow_location),
                onAction = { onEvent(HomeUiEvent.RequestLocation) },
                secondaryAction = {
                    OutlinedButton(onClick = { onEvent(HomeUiEvent.OpenCities) }) {
                        Text(stringResource(R.string.home_search_city))
                    }
                },
                modifier = content,
            )

            is HomeUiState.Error -> ErrorState(state.error, onRetry = {
                onEvent(HomeUiEvent.Retry)
            }, modifier = content)

            is HomeUiState.Content -> PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onEvent(HomeUiEvent.Refresh) },
                modifier = content.fillMaxSize(),
            ) {
                HomeContent(state)
            }
        }
    }
}

@Composable
private fun HomeContent(state: HomeUiState.Content) {
    val format = rememberWeatherFormatter()
    val spacing = AtmosTheme.spacing
    LazyColumn(
        contentPadding = PaddingValues(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
        modifier = Modifier.fillMaxSize(),
    ) {
        state.outdatedBy?.let { age -> item(key = "banner") { InfoBanner(updatedAgo(age)) } }
        item(key = "current") { CurrentConditions(state.forecast.current, state.settings, format) }
        item(key = "hourly-title") { SectionTitle(stringResource(R.string.home_next_24_hours)) }
        item(key = "hourly") { HourlyRow(state.forecast.hourly, state.settings, format) }
        item(key = "daily-title") { SectionTitle(stringResource(R.string.home_next_7_days)) }
        items(state.forecast.daily, key = { it.date.toString() }) { day ->
            DailyRow(day, isToday = day == state.forecast.daily.first(), settings = state.settings, format = format)
            HorizontalDivider()
        }
        item(key = "attribution") {
            Text(
                stringResource(R.string.home_attribution),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = spacing.small),
            )
        }
    }
}

@Composable
private fun CurrentConditions(current: CurrentWeather, settings: UserSettings, format: WeatherFormatter) {
    val condition = stringResource(current.condition.labelRes())
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AtmosTheme.spacing.extraSmall),
        modifier = Modifier.fillMaxWidth(),
    ) {
        WeatherIcon(current.condition.iconRes(current.isDay), contentDescription = null, size = 96.dp)
        Text(
            format.temperature(current.temperature, settings.temperatureUnit),
            style = MaterialTheme.typography.displayLarge,
        )
        Text(condition, style = MaterialTheme.typography.titleLarge)
        Text(
            stringResource(R.string.home_feels_like, format.temperature(current.feelsLike, settings.temperatureUnit)),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            stringResource(R.string.home_humidity, format.percentage(current.humidityPercent)) + " · " +
                stringResource(R.string.home_wind, format.windSpeed(current.windSpeed, settings.windSpeedUnit)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/** A horizontal list. In Arabic it starts on the right and scrolls left, automatically. */
@Composable
private fun HourlyRow(hours: List<HourlyForecast>, settings: UserSettings, format: WeatherFormatter) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(AtmosTheme.spacing.small)) {
        items(hours, key = { it.time.toString() }) { hour ->
            val isNow = hour == hours.first()
            val time = if (isNow) stringResource(R.string.home_now) else format.hour(hour.time)
            val temperature = format.temperature(hour.temperature, settings.temperatureUnit)
            val chance = hour.precipitationChancePercent?.takeIf { it > 0 }?.let(format::percentage)
            val description = listOfNotNull(
                time,
                stringResource(hour.condition.labelRes()),
                temperature,
                chance?.let { stringResource(R.string.home_precipitation_chance, it) },
            ).joinToString(", ")
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainer,
                // One clear sentence for screen readers instead of four fragments.
                modifier = Modifier.clearAndSetSemantics { contentDescription = description },
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AtmosTheme.spacing.extraSmall),
                    modifier = Modifier.padding(AtmosTheme.spacing.small).width(56.dp),
                ) {
                    Text(time, style = MaterialTheme.typography.labelMedium)
                    WeatherIcon(hour.condition.iconRes(), contentDescription = null)
                    Text(temperature, style = MaterialTheme.typography.titleMedium.merge(TabularNumbers))
                    // Hidden at 0% to reduce noise (US-02); the space is kept so cards line up.
                    Text(
                        chance.orEmpty(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

/**
 * One day of the 7-day list. Tapping it shows sunrise and sunset (US-03). Whether a row is
 * open is pure UI state: it lives here, in `rememberSaveable`, not in the ViewModel.
 */
@Composable
private fun DailyRow(day: DailyForecast, isToday: Boolean, settings: UserSettings, format: WeatherFormatter) {
    var expanded by rememberSaveable(day.date) { mutableStateOf(false) }
    val name = if (isToday) stringResource(R.string.home_today) else format.dayName(day.date.dayOfWeek)
    val min = format.temperature(day.minTemperature, settings.temperatureUnit)
    val max = format.temperature(day.maxTemperature, settings.temperatureUnit)
    val sunrise = day.sunrise
    val sunset = day.sunset
    val sunTimes = if (sunrise != null && sunset != null) {
        stringResource(R.string.home_sunrise_sunset, format.hour(sunrise), format.hour(sunset))
    } else {
        null
    }
    val description = listOfNotNull(
        name,
        stringResource(day.condition.labelRes()),
        stringResource(R.string.home_min_max, min, max),
        sunTimes?.takeIf { expanded },
    ).joinToString(", ")
    val state = stringResource(if (expanded) R.string.state_expanded else R.string.state_collapsed)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = sunTimes != null, role = Role.Button) { expanded = !expanded }
            .animateContentSize()
            .clearAndSetSemantics {
                contentDescription = description
                if (sunTimes != null) stateDescription = state
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AtmosTheme.spacing.medium),
            modifier = Modifier.fillMaxWidth().height(AtmosTheme.spacing.touchTarget + AtmosTheme.spacing.small),
        ) {
            Text(name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            WeatherIcon(day.condition.iconRes(), contentDescription = null)
            Text(
                min,
                style = MaterialTheme.typography.bodyLarge.merge(TabularNumbers),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(max, style = MaterialTheme.typography.bodyLarge.merge(TabularNumbers))
        }
        if (expanded && sunTimes != null) {
            Text(
                sunTimes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = AtmosTheme.spacing.small),
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
}

/** "Updated 2 hours ago", with the right plural form in every language. */
@Composable
private fun updatedAgo(age: Duration): String = when {
    age.toHours() >= HOURS_PER_DAY ->
        pluralStringResource(R.plurals.home_updated_days_ago, age.toDays().toInt(), age.toDays().toInt())

    age.toMinutes() >= MINUTES_PER_HOUR ->
        pluralStringResource(R.plurals.home_updated_hours_ago, age.toHours().toInt(), age.toHours().toInt())

    else -> pluralStringResource(R.plurals.home_updated_minutes_ago, age.toMinutes().toInt(), age.toMinutes().toInt())
}

private const val HOURS_PER_DAY = 24
private const val MINUTES_PER_HOUR = 60

/** Same layout as the real content, in grey (US-09). */
@Composable
private fun HomeSkeleton(modifier: Modifier = Modifier) {
    val spacing = AtmosTheme.spacing
    Column(
        modifier = modifier.fillMaxSize().padding(spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
    ) {
        SkeletonBlock(Modifier.size(96.dp))
        SkeletonBlock(Modifier.size(width = 160.dp, height = 96.dp))
        SkeletonBlock(Modifier.size(width = 200.dp, height = 24.dp))
        Spacer(Modifier.height(spacing.medium))
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
            repeat(5) { SkeletonBlock(Modifier.size(width = 64.dp, height = 112.dp)) }
        }
        repeat(4) { SkeletonBlock(Modifier.fillMaxWidth().height(48.dp)) }
    }
}

/** Every state of the screen, so each one gets the four [AtmosPreviews]. */
internal class HomeStateProvider : PreviewParameterProvider<HomeUiState> {
    override val values = sequenceOf(
        HomeUiState.Content(placeName = "İstanbul", forecast = PreviewData.forecast, settings = UserSettings()),
        HomeUiState.Content(
            placeName = null,
            forecast = PreviewData.forecast,
            settings = UserSettings(),
            outdatedBy = Duration.ofHours(2),
        ),
        HomeUiState.Loading,
        HomeUiState.NeedsLocation,
        HomeUiState.Error(DomainError.NoConnection),
    )
}

@AtmosPreviews
@Composable
private fun HomeScreenPreview(@PreviewParameter(HomeStateProvider::class) state: HomeUiState) {
    AtmosTheme(dynamicColor = false) { HomeScreen(state = state, onEvent = {}) }
}
