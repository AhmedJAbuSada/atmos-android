package dev.ahmedabusada.atmos.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A saved city (F-06). The geocoding id is the primary key, so the database itself makes
 * duplicates impossible. [addedAt] keeps the list in the order the user added cities.
 */
@Entity(tableName = "saved_city")
internal data class SavedCityEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val region: String?,
    val country: String?,
    @ColumnInfo(name = "country_code") val countryCode: String?,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "added_at") val addedAt: Long,
)

/**
 * The last forecast downloaded for a place (F-08).
 *
 * We store the **raw JSON** and map it again on read, instead of one table per entity.
 * The forecast is always read and written as a whole, never queried by field, so a
 * normalized schema would add many tables and migrations for no benefit. And because the
 * same mapper runs on fresh and cached data, they can never disagree.
 */
@Entity(tableName = "forecast_cache")
internal data class ForecastCacheEntity(
    /** See [cacheKey]. */
    @PrimaryKey @ColumnInfo(name = "location_key") val locationKey: String,
    val json: String,
    @ColumnInfo(name = "fetched_at") val fetchedAtEpochMillis: Long,
)
