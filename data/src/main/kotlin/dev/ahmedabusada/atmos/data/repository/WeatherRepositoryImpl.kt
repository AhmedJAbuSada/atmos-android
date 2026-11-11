package dev.ahmedabusada.atmos.data.repository

import dev.ahmedabusada.atmos.data.di.IoDispatcher
import dev.ahmedabusada.atmos.data.local.ForecastCacheDao
import dev.ahmedabusada.atmos.data.local.ForecastCacheEntity
import dev.ahmedabusada.atmos.data.local.cacheKey
import dev.ahmedabusada.atmos.data.mapper.toDomain
import dev.ahmedabusada.atmos.data.remote.AtmosJson
import dev.ahmedabusada.atmos.data.remote.ForecastApi
import dev.ahmedabusada.atmos.data.remote.dto.ForecastDto
import dev.ahmedabusada.atmos.data.remote.toDomainError
import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.common.runCatchingDomain
import dev.ahmedabusada.atmos.domain.location.Coordinates
import dev.ahmedabusada.atmos.domain.weather.Forecast
import dev.ahmedabusada.atmos.domain.weather.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

/**
 * [WeatherRepository] on Open-Meteo (network) and Room (cache).
 *
 * It only stores and fetches; *when* to fetch is decided by `GetForecastUseCase` in the
 * domain. It switches to the IO dispatcher itself, so callers never have to think about
 * threads (ARCHITECTURE §6).
 */
internal class WeatherRepositoryImpl @Inject constructor(
    private val api: ForecastApi,
    private val cache: ForecastCacheDao,
    private val clock: Clock,
    @param:IoDispatcher private val io: CoroutineDispatcher,
) : WeatherRepository {
    override suspend fun getCachedForecast(coordinates: Coordinates): Forecast? = withContext(io) {
        val entry = cache.get(coordinates.cacheKey()) ?: return@withContext null
        // A cache entry that no longer parses (for example after an app update changed
        // the DTOs) is treated as "no cache", never as a crash.
        (
            runCatchingDomain {
                AtmosJson.decodeFromString<ForecastDto>(entry.json)
                    .toDomain(coordinates, Instant.ofEpochMilli(entry.fetchedAtEpochMillis))
            } as? AppResult.Success
            )?.data
    }

    override suspend fun fetchForecast(coordinates: Coordinates): AppResult<Forecast> = withContext(io) {
        runCatchingDomain(mapError = Throwable::toDomainError) {
            val dto = api.getForecast(coordinates.latitude, coordinates.longitude)
            val fetchedAt = Instant.now(clock)
            val forecast = dto.toDomain(coordinates, fetchedAt) // validate before caching
            cache.upsert(
                ForecastCacheEntity(
                    locationKey = coordinates.cacheKey(),
                    json = AtmosJson.encodeToString(dto),
                    fetchedAtEpochMillis = fetchedAt.toEpochMilli(),
                ),
            )
            forecast
        }
    }
}
