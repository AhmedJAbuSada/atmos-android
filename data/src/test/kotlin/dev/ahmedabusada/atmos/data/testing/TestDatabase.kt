package dev.ahmedabusada.atmos.data.testing

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.ahmedabusada.atmos.data.local.AtmosDatabase

/** A real Room database in memory. Runs on the JVM through Robolectric; no emulator. */
internal fun inMemoryDatabase(): AtmosDatabase = Room.inMemoryDatabaseBuilder(
    ApplicationProvider.getApplicationContext(),
    AtmosDatabase::class.java,
).allowMainThreadQueries().build()
