package dev.ahmedabusada.atmos.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/** One JSON configuration for the whole data layer. */
internal val AtmosJson = Json {
    // The API may add fields at any time; that must not break the app.
    ignoreUnknownKeys = true
    // A `null` where we expect a default (for example `"time": null`) uses the default.
    coerceInputValues = true
}

/** OkHttp with the client rules from API_CONTRACT §4. */
internal fun createOkHttpClient(userAgent: String, retry: RetryInterceptor = RetryInterceptor()): OkHttpClient =
    OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            chain.proceed(chain.request().newBuilder().header("User-Agent", userAgent).build())
        }
        .addInterceptor(retry)
        .build()

internal fun createRetrofit(baseUrl: String, client: OkHttpClient): Retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(client)
    .addConverterFactory(AtmosJson.asConverterFactory("application/json".toMediaType()))
    .build()
