package dev.ahmedabusada.atmos.data.remote

import dev.ahmedabusada.atmos.data.remote.dto.ForecastDto
import dev.ahmedabusada.atmos.data.remote.dto.GeocodingDto
import retrofit2.http.GET
import retrofit2.http.Query

/** `https://api.open-meteo.com/v1/forecast` (API_CONTRACT §1). */
internal interface ForecastApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = CURRENT_FIELDS,
        @Query("hourly") hourly: String = HOURLY_FIELDS,
        @Query("daily") daily: String = DAILY_FIELDS,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("forecast_hours") forecastHours: Int = 24,
    ): ForecastDto

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/"

        // Ask only for what the screens show: a smaller response is faster to download.
        const val CURRENT_FIELDS =
            "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,weather_code,wind_speed_10m"
        const val HOURLY_FIELDS = "temperature_2m,weather_code,precipitation_probability"
        const val DAILY_FIELDS = "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset"
    }
}

/** `https://geocoding-api.open-meteo.com/v1/search` (API_CONTRACT §2). */
internal interface GeocodingApi {
    @GET("v1/search")
    suspend fun search(
        @Query("name") name: String,
        @Query("language") language: String,
        @Query("count") count: Int = 10,
        @Query("format") format: String = "json",
    ): GeocodingDto

    companion object {
        const val BASE_URL = "https://geocoding-api.open-meteo.com/"
    }
}
