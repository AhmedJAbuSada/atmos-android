package dev.ahmedabusada.atmos.data.repository

import dev.ahmedabusada.atmos.data.di.IoDispatcher
import dev.ahmedabusada.atmos.data.local.SavedCityDao
import dev.ahmedabusada.atmos.data.local.SavedCityEntity
import dev.ahmedabusada.atmos.data.mapper.toDomain
import dev.ahmedabusada.atmos.data.remote.GeocodingApi
import dev.ahmedabusada.atmos.data.remote.toDomainError
import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.common.runCatchingDomain
import dev.ahmedabusada.atmos.domain.location.City
import dev.ahmedabusada.atmos.domain.location.CityRepository
import dev.ahmedabusada.atmos.domain.location.Coordinates
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Clock
import javax.inject.Inject

/** [CityRepository]: search on Open-Meteo geocoding, favourites in Room. */
internal class CityRepositoryImpl @Inject constructor(
    private val api: GeocodingApi,
    private val dao: SavedCityDao,
    private val clock: Clock,
    @param:IoDispatcher private val io: CoroutineDispatcher,
) : CityRepository {
    override suspend fun search(query: String, languageTag: String): AppResult<List<City>> = withContext(io) {
        runCatchingDomain(mapError = Throwable::toDomainError) {
            api.search(name = query, language = languageTag).toDomain()
        }
    }

    override fun observeSavedCities(): Flow<List<City>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun save(city: City) = withContext(io) {
        dao.insert(city.toEntity(addedAt = clock.millis()))
    }

    override suspend fun remove(cityId: Long) = withContext(io) { dao.delete(cityId) }
}

private fun SavedCityEntity.toDomain() = City(
    id = id,
    name = name,
    region = region,
    country = country,
    countryCode = countryCode,
    coordinates = Coordinates(latitude, longitude),
)

private fun City.toEntity(addedAt: Long) = SavedCityEntity(
    id = id,
    name = name,
    region = region,
    country = country,
    countryCode = countryCode,
    latitude = coordinates.latitude,
    longitude = coordinates.longitude,
    addedAt = addedAt,
)
