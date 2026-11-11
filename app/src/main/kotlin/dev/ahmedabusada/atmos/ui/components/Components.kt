package dev.ahmedabusada.atmos.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ahmedabusada.atmos.R
import dev.ahmedabusada.atmos.domain.common.DomainError
import dev.ahmedabusada.atmos.ui.preview.AtmosPreviews
import dev.ahmedabusada.atmos.ui.theme.AtmosTheme
import dev.ahmedabusada.atmos.ui.weather.messageRes

/**
 * A weather icon. [contentDescription] is null when the condition is already written next
 * to the icon, so a screen reader does not say it twice (NF-03).
 */
@Composable
fun WeatherIcon(@DrawableRes icon: Int, contentDescription: String?, modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Image(
        painter = painterResource(icon),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
    )
}

/** A full-screen message with an optional action, used for errors and empty states. */
@Composable
fun MessageState(
    message: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    secondaryAction: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(AtmosTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(AtmosTheme.spacing.medium, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (title != null) Text(title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        if (actionLabel != null && onAction != null) Button(onClick = onAction) { Text(actionLabel) }
        secondaryAction?.invoke()
    }
}

/** The standard error screen: the human message for [error] and a retry button (F-10). */
@Composable
fun ErrorState(error: DomainError, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    MessageState(
        message = stringResource(error.messageRes()),
        actionLabel = stringResource(R.string.action_retry),
        onAction = onRetry,
        modifier = modifier,
    )
}

/** The "last updated 2 h ago" strip shown over outdated data (F-08). */
@Composable
fun InfoBanner(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(AtmosTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(AtmosTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(painterResource(R.drawable.ic_info), contentDescription = null)
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/**
 * A grey block that gently pulses while content loads (US-09: a skeleton, not a blank
 * screen). Screens lay these out in the same shape as the real content, so nothing jumps
 * when the data arrives.
 */
@Composable
fun SkeletonBlock(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 800), RepeatMode.Reverse),
        label = "skeleton-alpha",
    )
    Surface(
        modifier = modifier.alpha(alpha),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        content = {},
    )
}

@AtmosPreviews
@Composable
private fun ComponentsPreview() {
    AtmosTheme(dynamicColor = false) {
        Surface {
            Column(
                Modifier.padding(16.dp).background(MaterialTheme.colorScheme.surface),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        R.drawable.ic_weather_clear_day,
                        R.drawable.ic_weather_clear_night,
                        R.drawable.ic_weather_partly_cloudy_day,
                        R.drawable.ic_weather_cloudy,
                        R.drawable.ic_weather_rain,
                        R.drawable.ic_weather_snow,
                        R.drawable.ic_weather_thunderstorm,
                        R.drawable.ic_weather_fog,
                    ).forEach { WeatherIcon(it, contentDescription = null) }
                }
                InfoBanner("Updated 2 hours ago")
                SkeletonBlock(Modifier.fillMaxWidth().size(48.dp))
            }
        }
    }
}
