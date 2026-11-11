package dev.ahmedabusada.atmos.data.local

import dev.ahmedabusada.atmos.domain.location.Coordinates
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class CacheKeyTest {
    private val original = Locale.getDefault()

    @After
    fun restoreLocale() = Locale.setDefault(original)

    @Test
    fun `rounds to 2 decimals so nearby positions share a cache entry`() {
        assertEquals("41.01,28.97", Coordinates(41.0123, 28.9711).cacheKey())
        assertEquals(Coordinates(41.0101, 28.9702).cacheKey(), Coordinates(41.0149, 28.9749).cacheKey())
    }

    @Test
    fun `the key is the same on Turkish and Arabic phones`() {
        // With the default locale, "%.2f" gives "41,01" in Turkish and Arabic-Indic
        // digits in Arabic. The cache would then be lost when the user changes language.
        listOf("tr", "ar", "en").forEach { tag ->
            Locale.setDefault(Locale.forLanguageTag(tag))
            assertEquals("locale $tag", "41.01,28.97", Coordinates(41.01, 28.97).cacheKey())
        }
    }
}
