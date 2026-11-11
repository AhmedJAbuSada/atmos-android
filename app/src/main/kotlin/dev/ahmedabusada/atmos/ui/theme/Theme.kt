package dev.ahmedabusada.atmos.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import dev.ahmedabusada.atmos.domain.settings.AppTheme

/**
 * The app theme. [appTheme] comes from the user's settings (F-09): follow the system, or
 * force light or dark. On Android 12+ the colours follow the wallpaper ("dynamic colour")
 * unless [dynamicColor] is false, which previews and screenshot tests use for stable
 * output.
 */
@Composable
fun AtmosTheme(appTheme: AppTheme = AppTheme.System, dynamicColor: Boolean = true, content: @Composable () -> Unit) {
    val dark = when (appTheme) {
        AppTheme.System -> isSystemInDarkTheme()
        AppTheme.Light -> false
        AppTheme.Dark -> true
    }
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        dark -> DarkColors

        else -> LightColors
    }
    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(colorScheme = colors, typography = AtmosTypography, content = content)
    }
}

/** Access to the app's own tokens, next to `MaterialTheme.colorScheme` and friends. */
object AtmosTheme {
    val spacing: Spacing
        @Composable get() = LocalSpacing.current
}
