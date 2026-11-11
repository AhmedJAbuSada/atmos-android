package dev.ahmedabusada.atmos.data.mapper

import dev.ahmedabusada.atmos.data.remote.AtmosJson
import dev.ahmedabusada.atmos.data.remote.dto.CityDto
import dev.ahmedabusada.atmos.data.remote.dto.GeocodingDto
import dev.ahmedabusada.atmos.data.testing.Samples
import dev.ahmedabusada.atmos.domain.location.Coordinates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CityMapperTest {
    private fun citiesIn(sample: String) = AtmosJson.decodeFromString<GeocodingDto>(Samples.read(sample)).toDomain()

    @Test
    fun `maps the recorded Turkish search with Turkish names`() {
        val istanbul = citiesIn("geocoding_istanbul_tr.json").first()

        assertEquals(745044L, istanbul.id)
        assertEquals("İstanbul", istanbul.name)
        assertEquals("İstanbul", istanbul.region)
        assertEquals("TR", istanbul.countryCode)
        assertEquals(Coordinates(41.01384, 28.94966), istanbul.coordinates)
    }

    @Test
    fun `the same city has the same id in every language`() {
        val ids = listOf("en", "tr", "ar").map { citiesIn("geocoding_istanbul_$it.json").first().id }

        assertEquals(setOf(745044L), ids.toSet())
    }

    @Test
    fun `maps the recorded Arabic search with Arabic names`() {
        assertEquals("اسطنبول", citiesIn("geocoding_istanbul_ar.json").first().name)
    }

    @Test
    fun `given a response with no results key, then returns an empty list`() {
        // The real API omits "results" when nothing matches. This is the bug the
        // recorded sample was saved to catch.
        assertTrue(citiesIn("geocoding_empty.json").isEmpty())
    }

    @Test
    fun `drops results that cannot be shown or saved`() {
        val valid = CityDto(id = 1, name = "Ankara", latitude = 39.9, longitude = 32.8)

        assertNull(valid.copy(id = null).toDomainOrNull())
        assertNull(valid.copy(name = " ").toDomainOrNull())
        assertNull(valid.copy(latitude = null).toDomainOrNull())
        assertNull(valid.copy(latitude = 999.0).toDomainOrNull())
        assertEquals("Ankara", valid.toDomainOrNull()?.name)
    }

    @Test
    fun `blank region and country become null`() {
        val city = CityDto(id = 1, name = "X", latitude = 1.0, longitude = 1.0, admin1 = "", country = " ")
            .toDomainOrNull()

        assertNull(city?.region)
        assertNull(city?.country)
    }
}
