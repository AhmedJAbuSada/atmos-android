package dev.ahmedabusada.atmos.domain.fakes

import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.location.City
import dev.ahmedabusada.atmos.domain.location.CityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeCityRepository(var searchResult: AppResult<List<City>> = AppResult.Success(emptyList())) : CityRepository {
    private val saved = MutableStateFlow<List<City>>(emptyList())

    /** Every (query, languageTag) the use case sent to the "network". */
    val searches = mutableListOf<Pair<String, String>>()

    override suspend fun search(query: String, languageTag: String): AppResult<List<City>> {
        searches += query to languageTag
        return searchResult
    }

    override fun observeSavedCities(): Flow<List<City>> = saved

    override suspend fun save(city: City) = saved.update { list -> list.filterNot { it.id == city.id } + city }

    override suspend fun remove(cityId: Long) = saved.update { list -> list.filterNot { it.id == cityId } }
}
