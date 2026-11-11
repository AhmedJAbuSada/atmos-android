package dev.ahmedabusada.atmos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SavedCityDao {
    /** Room re-emits whenever the table changes, which keeps the Cities screen live. */
    @Query("SELECT * FROM saved_city ORDER BY added_at ASC")
    fun observeAll(): Flow<List<SavedCityEntity>>

    /** A city already saved keeps its original position in the list. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(city: SavedCityEntity)

    @Query("DELETE FROM saved_city WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
internal interface ForecastCacheDao {
    @Query("SELECT * FROM forecast_cache WHERE location_key = :key")
    suspend fun get(key: String): ForecastCacheEntity?

    @Upsert
    suspend fun upsert(entry: ForecastCacheEntity)
}
