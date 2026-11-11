package dev.ahmedabusada.atmos.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Brand colours: a calm sky blue and a warm sun yellow. Every pair below was checked for
// WCAG AA contrast (4.5:1 for text) in both themes (NF-03).
private val SkyBlue10 = Color(0xFF001B3E)
private val SkyBlue20 = Color(0xFF002F65)
private val SkyBlue30 = Color(0xFF00458E)
private val SkyBlue40 = Color(0xFF1E5AA8)
private val SkyBlue80 = Color(0xFFAAC7FF)
private val SkyBlue90 = Color(0xFFD6E3FF)
private val Sun30 = Color(0xFF5C4300)
private val Sun40 = Color(0xFF7A5900)
private val Sun80 = Color(0xFFF6BE2E)
private val Sun90 = Color(0xFFFFDF9C)
private val Neutral10 = Color(0xFF1A1C20)
private val Neutral12 = Color(0xFF1E2024)
private val Neutral17 = Color(0xFF282A2F)
private val Neutral90 = Color(0xFFE2E2E9)
private val Neutral95 = Color(0xFFF0F0F7)
private val Neutral98 = Color(0xFFF9F9FF)
private val NeutralVariant30 = Color(0xFF44474F)
private val NeutralVariant80 = Color(0xFFC4C6D0)
private val Error40 = Color(0xFFBA1A1A)
private val Error80 = Color(0xFFFFB4AB)

internal val LightColors = lightColorScheme(
    primary = SkyBlue40,
    onPrimary = Color.White,
    primaryContainer = SkyBlue90,
    onPrimaryContainer = SkyBlue10,
    secondary = Sun40,
    onSecondary = Color.White,
    secondaryContainer = Sun90,
    onSecondaryContainer = Sun30,
    background = Neutral98,
    onBackground = Neutral10,
    surface = Neutral98,
    onSurface = Neutral10,
    surfaceVariant = Neutral90,
    onSurfaceVariant = NeutralVariant30,
    surfaceContainer = Neutral95,
    error = Error40,
)

internal val DarkColors = darkColorScheme(
    primary = SkyBlue80,
    onPrimary = SkyBlue20,
    primaryContainer = SkyBlue30,
    onPrimaryContainer = SkyBlue90,
    secondary = Sun80,
    onSecondary = Sun30,
    secondaryContainer = Sun30,
    onSecondaryContainer = Sun90,
    background = Neutral10,
    onBackground = Neutral90,
    surface = Neutral10,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    surfaceContainer = Neutral12,
    surfaceContainerHigh = Neutral17,
    error = Error80,
)
