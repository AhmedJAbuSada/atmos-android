package dev.ahmedabusada.atmos.data.mapper

import dev.ahmedabusada.atmos.data.remote.dto.CityDto
import dev.ahmedabusada.atmos.data.remote.dto.GeocodingDto
import dev.ahmedabusada.atmos.domain.location.City
import dev.ahmedabusada.atmos.domain.location.Coordinates

/** Results without an id, a name or valid coordinates are dropped: we cannot show or save them. */
internal fun GeocodingDto.toDomain(): List<City> = results.mapNotNull(CityDto::toDomainOrNull)

internal fun CityDto.toDomainOrNull(): City? {
    val coordinates = try {
        Coordinates(latitude ?: return null, longitude ?: return null)
    } catch (_: IllegalArgumentException) {
        return null // out of range: Coordinates refuses to exist, so we skip the result
    }
    return City(
        id = id ?: return null,
        name = name?.takeIf(String::isNotBlank) ?: return null,
        region = admin1?.takeIf(String::isNotBlank),
        country = country?.takeIf(String::isNotBlank),
        countryCode = countryCode,
        coordinates = coordinates,
    )
}
