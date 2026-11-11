package dev.ahmedabusada.atmos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The app's database. Version 1. The schema is exported to `data/schemas/`, so every
 * future change shows up in code review, and needs a migration (tested in step 07).
 */
@Database(
    entities = [SavedCityEntity::class, ForecastCacheEntity::class],
    version = 1,
    exportSchema = true,
)
internal abstract class AtmosDatabase : RoomDatabase() {
    abstract fun savedCityDao(): SavedCityDao

    abstract fun forecastCacheDao(): ForecastCacheDao

    companion object {
        const val NAME = "atmos.db"
    }
}
