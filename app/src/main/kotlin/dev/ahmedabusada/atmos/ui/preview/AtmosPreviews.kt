package dev.ahmedabusada.atmos.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * One annotation, four previews: every screen is checked in light, dark, Arabic (RTL) and
 * with the largest font size (NF-03), side by side in Android Studio. A layout bug in RTL
 * or at 200% text is visible before anyone runs the app.
 */
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Arabic RTL", showBackground = true, locale = "ar")
@Preview(name = "Turkish, font 200%", showBackground = true, locale = "tr", fontScale = 2f)
annotation class AtmosPreviews
