package dev.ahmedabusada.atmos.feature.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dev.ahmedabusada.atmos.R
import dev.ahmedabusada.atmos.ui.preview.AtmosPreviews
import dev.ahmedabusada.atmos.ui.theme.AtmosTheme

/** Links shown on the About screen (F-11). */
object AboutLinks {
    const val OPEN_METEO = "https://open-meteo.com/"
    const val SOURCE_CODE = "https://github.com/AhmedJAbuSada/atmos-android"
}

/** About (US-10): attribution required by the Open-Meteo licence, version, source code. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(versionName: String, onOpenLink: (String) -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), stringResource(R.string.action_back))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(AtmosTheme.spacing.medium).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AtmosTheme.spacing.small),
        ) {
            Image(
                painterResource(R.drawable.ic_weather_partly_cloudy_day),
                contentDescription = null,
                Modifier.size(96.dp),
            )
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.about_version, versionName), style = MaterialTheme.typography.bodyMedium)
            LinkRow(stringResource(R.string.about_data_source)) { onOpenLink(AboutLinks.OPEN_METEO) }
            LinkRow(stringResource(R.string.about_source_code)) { onOpenLink(AboutLinks.SOURCE_CODE) }
            Text(stringResource(R.string.about_license), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun LinkRow(text: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(text) },
        trailingContent = {
            Icon(painterResource(R.drawable.ic_open_in_new), stringResource(R.string.action_open_link))
        },
        modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
    )
}

@AtmosPreviews
@Composable
private fun AboutScreenPreview() {
    AtmosTheme(dynamicColor = false) { AboutScreen(versionName = "0.5.0", onOpenLink = {}, onBack = {}) }
}
