package dev.ahmedabusada.atmos.domain.fakes

import dev.ahmedabusada.atmos.domain.common.AppResult
import dev.ahmedabusada.atmos.domain.location.Coordinates
import dev.ahmedabusada.atmos.domain.location.LocationRepository

class FakeLocationRepository(var result: AppResult<Coordinates> = AppResult.Success(TestData.ISTANBUL)) :
    LocationRepository {
    override suspend fun getCurrentLocation(): AppResult<Coordinates> = result
}
