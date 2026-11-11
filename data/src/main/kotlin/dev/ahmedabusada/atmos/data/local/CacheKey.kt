package dev.ahmedabusada.atmos.data.local

import dev.ahmedabusada.atmos.domain.location.Coordinates
import java.util.Locale

/**
 * The cache key for a place: coordinates rounded to 2 decimals (about 1 km), so tiny GPS
 * changes reuse the same cache entry.
 *
 * `Locale.ROOT` is essential. With the default locale on a Turkish or Arabic phone,
 * `"%.2f".format(41.01)` becomes `"41,01"` or `"٤١٫٠١"`, and the cache written in one
 * language would never be found in another.
 */
internal fun Coordinates.cacheKey(): String = String.format(Locale.ROOT, "%.2f,%.2f", latitude, longitude)
