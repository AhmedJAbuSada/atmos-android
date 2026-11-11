package dev.ahmedabusada.atmos.domain.location

/**
 * A city from the geocoding search (F-05) or the saved list (F-06).
 *
 * [id] comes from the geocoding service and is stable, so it identifies a saved city
 * (no duplicates, US-05). [name], [region] and [country] are already in the language
 * that was used for the search.
 */
data class City(
    val id: Long,
    val name: String,
    val region: String?,
    val country: String?,
    val countryCode: String?,
    val coordinates: Coordinates,
)

/**
 * An entry in the places list. The current location is always first and cannot be
 * removed (US-05); saved cities follow in the order they were added.
 */
sealed interface Place {
    data object CurrentLocation : Place

    data class Saved(val city: City) : Place
}
