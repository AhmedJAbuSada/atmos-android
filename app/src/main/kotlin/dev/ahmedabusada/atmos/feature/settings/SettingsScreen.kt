package dev.ahmedabusada.atmos.feature.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import dev.ahmedabusada.atmos.R
import dev.ahmedabusada.atmos.domain.settings.AppLanguage
import dev.ahmedabusada.atmos.domain.settings.AppTheme
import dev.ahmedabusada.atmos.domain.settings.TemperatureUnit
import dev.ahmedabusada.atmos.domain.settings.UserSettings
import dev.ahmedabusada.atmos.domain.settings.WindSpeedUnit
import dev.ahmedabusada.atmos.ui.preview.AtmosPreviews
import dev.ahmedabusada.atmos.ui.theme.AtmosTheme

sealed interface SettingsUiEvent {
    data class SetTemperatureUnit(val unit: TemperatureUnit) : SettingsUiEvent

    data class SetWindSpeedUnit(val unit: WindSpeedUnit) : SettingsUiEvent

    data class SetLanguage(val language: AppLanguage) : SettingsUiEvent

    data class SetTheme(val theme: AppTheme) : SettingsUiEvent

    data object OpenAbout : SettingsUiEvent

    data object Back : SettingsUiEvent
}

/** Settings (US-08). The state is simply the domain's [UserSettings]: nothing to add. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(settings: UserSettings, onEvent: (SettingsUiEvent) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(SettingsUiEvent.Back) }) {
                        Icon(painterResource(R.drawable.ic_arrow_back), stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(AtmosTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(AtmosTheme.spacing.medium),
        ) {
            Segmented(
                title = R.string.settings_temperature,
                options = listOf(
                    TemperatureUnit.Celsius to R.string.unit_celsius,
                    TemperatureUnit.Fahrenheit to R.string.unit_fahrenheit,
                ),
                selected = settings.temperatureUnit,
                onSelect = { onEvent(SettingsUiEvent.SetTemperatureUnit(it)) },
            )
            Segmented(
                title = R.string.settings_wind_speed,
                options = listOf(
                    WindSpeedUnit.KilometersPerHour to R.string.unit_kmh,
                    WindSpeedUnit.MetersPerSecond to R.string.unit_ms,
                    WindSpeedUnit.MilesPerHour to R.string.unit_mph,
                ),
                selected = settings.windSpeedUnit,
                onSelect = { onEvent(SettingsUiEvent.SetWindSpeedUnit(it)) },
            )
            RadioGroup(
                title = R.string.settings_language,
                options = listOf(
                    AppLanguage.System to R.string.language_system,
                    AppLanguage.English to R.string.language_english,
                    AppLanguage.Turkish to R.string.language_turkish,
                    AppLanguage.Arabic to R.string.language_arabic,
                ),
                selected = settings.language,
                onSelect = { onEvent(SettingsUiEvent.SetLanguage(it)) },
            )
            RadioGroup(
                title = R.string.settings_theme,
                options = listOf(
                    AppTheme.System to R.string.theme_system,
                    AppTheme.Light to R.string.theme_light,
                    AppTheme.Dark to R.string.theme_dark,
                ),
                selected = settings.theme,
                onSelect = { onEvent(SettingsUiEvent.SetTheme(it)) },
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_about)) },
                trailingContent = { Icon(painterResource(R.drawable.ic_chevron_end), contentDescription = null) },
                modifier = Modifier.clickable(role = Role.Button) { onEvent(SettingsUiEvent.OpenAbout) },
            )
        }
    }
}

@Composable
private fun <T> Segmented(@StringRes title: Int, options: List<Pair<T, Int>>, selected: T, onSelect: (T) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(AtmosTheme.spacing.small)) {
        SectionTitle(title)
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, (value, label) ->
                SegmentedButton(
                    selected = value == selected,
                    onClick = { onSelect(value) },
                    shape = SegmentedButtonDefaults.itemShape(index, options.size),
                ) { Text(stringResource(label)) }
            }
        }
    }
}

/** Radio buttons in a `selectableGroup`, so screen readers announce "1 of 4, selected". */
@Composable
private fun <T> RadioGroup(@StringRes title: Int, options: List<Pair<T, Int>>, selected: T, onSelect: (T) -> Unit) {
    Column {
        SectionTitle(title)
        Column(Modifier.selectableGroup()) {
            options.forEach { (value, label) ->
                ListItem(
                    headlineContent = { Text(stringResource(label)) },
                    leadingContent = { RadioButton(selected = value == selected, onClick = null) },
                    modifier = Modifier.selectable(
                        selected = value == selected,
                        role = Role.RadioButton,
                        onClick = { onSelect(value) },
                    ),
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(@StringRes title: Int) {
    Text(
        stringResource(title),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.semantics { heading() },
    )
}

@AtmosPreviews
@Composable
private fun SettingsScreenPreview() {
    AtmosTheme(dynamicColor = false) {
        SettingsScreen(UserSettings(language = AppLanguage.Turkish, theme = AppTheme.Dark), onEvent = {})
    }
}
