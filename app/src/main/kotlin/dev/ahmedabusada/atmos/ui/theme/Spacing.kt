package dev.ahmedabusada.atmos.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing tokens on a 4 dp grid. Screens use `AtmosTheme.spacing.medium`, never a raw
 * `16.dp`, so the rhythm of the whole app can change in one place.
 */
@Immutable
data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    /** Minimum touch target (NF-03). */
    val touchTarget: Dp = 48.dp,
)

internal val LocalSpacing = staticCompositionLocalOf { Spacing() }
