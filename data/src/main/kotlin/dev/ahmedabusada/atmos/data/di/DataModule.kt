package dev.ahmedabusada.atmos.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ahmedabusada.atmos.data.local.AtmosDatabase
import dev.ahmedabusada.atmos.data.local.ForecastCacheDao
import dev.ahmedabusada.atmos.data.local.SavedCityDao
import dev.ahmedabusada.atmos.data.location.AndroidLocationPermission
import dev.ahmedabusada.atmos.data.location.LocationPermission
import dev.ahmedabusada.atmos.data.location.LocationRepositoryImpl
import dev.ahmedabusada.atmos.data.remote.ForecastApi
import dev.ahmedabusada.atmos.data.remote.GeocodingApi
import dev.ahmedabusada.atmos.data.remote.createOkHttpClient
import dev.ahmedabusada.atmos.data.remote.createRetrofit
import dev.ahmedabusada.atmos.data.repository.CityRepositoryImpl
import dev.ahmedabusada.atmos.data.repository.WeatherRepositoryImpl
import dev.ahmedabusada.atmos.data.settings.SettingsRepositoryImpl
import dev.ahmedabusada.atmos.domain.location.CityRepository
import dev.ahmedabusada.atmos.domain.location.LocationRepository
import dev.ahmedabusada.atmos.domain.settings.SettingsRepository
import dev.ahmedabusada.atmos.domain.weather.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.create
import java.time.Clock
import javax.inject.Singleton

/**
 * Tells Hilt which :data class implements each :domain interface. This is the one place
 * where the inversion from ADR-0001 is wired up. Everything else in :data stays `internal`.
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {
    @Binds
    abstract fun weather(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    abstract fun cities(impl: CityRepositoryImpl): CityRepository

    @Binds
    abstract fun location(impl: LocationRepositoryImpl): LocationRepository

    @Binds
    abstract fun settings(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun locationPermission(impl: AndroidLocationPermission): LocationPermission
}

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {
    @Provides
    @Singleton
    fun okHttp(@UserAgent userAgent: String): OkHttpClient = createOkHttpClient(userAgent)

    // Open-Meteo serves the two APIs from different hosts, so there are two Retrofits
    // sharing one OkHttp client (one connection pool).
    @Provides
    @Singleton
    fun forecastApi(client: OkHttpClient): ForecastApi = createRetrofit(ForecastApi.BASE_URL, client).create()

    @Provides
    @Singleton
    fun geocodingApi(client: OkHttpClient): GeocodingApi = createRetrofit(GeocodingApi.BASE_URL, client).create()

    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): AtmosDatabase =
        Room.databaseBuilder(context, AtmosDatabase::class.java, AtmosDatabase.NAME).build()

    @Provides
    fun savedCityDao(database: AtmosDatabase): SavedCityDao = database.savedCityDao()

    @Provides
    fun forecastCacheDao(database: AtmosDatabase): ForecastCacheDao = database.forecastCacheDao()

    @Provides
    @Singleton
    fun settingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile("settings") }

    @Provides
    @Singleton
    fun fusedLocation(@ApplicationContext context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @IoDispatcher
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun clock(): Clock = Clock.systemUTC()
}
