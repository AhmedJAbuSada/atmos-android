package dev.ahmedabusada.atmos.data.testing

import dev.ahmedabusada.atmos.data.local.ForecastCacheDao
import dev.ahmedabusada.atmos.data.local.ForecastCacheEntity
import dev.ahmedabusada.atmos.data.local.SavedCityDao
import dev.ahmedabusada.atmos.data.local.SavedCityEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * In-memory DAOs with the same behaviour as the Room ones (checked in `DaoTest`
 * against the real database). Repository tests use these, so they run on the plain JVM
 * in milliseconds; Room itself is tested once, in its own test.
 */
internal class FakeSavedCityDao : SavedCityDao {
    private val rows = MutableStateFlow<Map<Long, SavedCityEntity>>(emptyMap())

    override fun observeAll(): Flow<List<SavedCityEntity>> = rows.map { it.values.sortedBy(SavedCityEntity::addedAt) }

    override suspend fun insert(city: SavedCityEntity) = rows.update {
        if (city.id in
            it
        ) {
            it
        } else {
            it + (city.id to city)
        }
    }

    override suspend fun delete(id: Long) = rows.update { it - id }
}

internal class FakeForecastCacheDao : ForecastCacheDao {
    private val rows = mutableMapOf<String, ForecastCacheEntity>()

    override suspend fun get(key: String): ForecastCacheEntity? = rows[key]

    override suspend fun upsert(entry: ForecastCacheEntity) {
        rows[entry.locationKey] = entry
    }
}
