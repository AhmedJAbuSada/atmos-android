package dev.ahmedabusada.atmos.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The geocoding response (API_CONTRACT §2).
 *
 * When nothing matches, the API **omits `results` entirely** instead of sending `[]`
 * (see docs/api/samples/geocoding_empty.json), so it defaults to an empty list.
 */
@Serializable
internal data class GeocodingDto(val results: List<CityDto> = emptyList())

@Serializable
internal data class CityDto(
    val id: Long? = null,
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerialName("country_code") val countryCode: String? = null,
    val country: String? = null,
    val admin1: String? = null,
)
