package dev.ahmedabusada.atmos.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Material 3 type scale with the system font. The system font matters for this app: it
 * already supports Latin, Turkish and Arabic glyphs well on every device, and it follows
 * the user's font-size setting (`sp`, NF-03).
 */
internal val AtmosTypography = Typography().run {
    copy(
        // The big temperature on the home screen.
        displayLarge = displayLarge.copy(fontSize = 96.sp, lineHeight = 104.sp, fontWeight = FontWeight.Light),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.SemiBold),
    )
}

/** Digits of equal width, so "11°" and "18°" line up in the 7-day list. */
internal val TabularNumbers = TextStyle(fontFeatureSettings = "tnum")
