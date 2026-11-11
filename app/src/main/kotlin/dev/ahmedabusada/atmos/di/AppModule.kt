package dev.ahmedabusada.atmos.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.ahmedabusada.atmos.BuildConfig
import dev.ahmedabusada.atmos.data.di.UserAgent

/** App-level values that only :app knows, such as its version. */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    /** Identifies the app to Open-Meteo, with no personal data (API_CONTRACT §4). */
    @Provides
    @UserAgent
    fun userAgent(): String = "Atmos/${BuildConfig.VERSION_NAME} (Android)"
}
