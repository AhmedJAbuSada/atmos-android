package dev.ahmedabusada.atmos.domain.location

/**
 * A point on Earth in decimal degrees. Invalid values are rejected when the object is
 * created, so the rest of the app never has to check them.
 */
data class Coordinates(val latitude: Double, val longitude: Double) {
    init {
        require(latitude in -90.0..90.0) { "Latitude must be in -90..90, was $latitude" }
        require(longitude in -180.0..180.0) { "Longitude must be in -180..180, was $longitude" }
    }
}
