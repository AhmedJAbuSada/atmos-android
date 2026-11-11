package dev.ahmedabusada.atmos.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Retries a request after a network error or an HTTP 5xx, with exponential back-off
 * (API_CONTRACT §4): wait [baseDelayMillis], then twice that, and so on.
 *
 * HTTP 4xx is **not** retried: it means our request is wrong, and sending it again will
 * not help.
 */
internal class RetryInterceptor(
    private val maxRetries: Int = 2,
    private val baseDelayMillis: Long = 1_000,
    private val sleep: (Long) -> Unit = Thread::sleep,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        while (true) {
            val response = try {
                chain.proceed(chain.request())
            } catch (exception: IOException) {
                if (attempt >= maxRetries || chain.call().isCanceled()) throw exception
                null
            }
            if (response != null && (response.code < SERVER_ERROR || attempt >= maxRetries)) return response

            response?.close()
            sleep(baseDelayMillis shl attempt)
            attempt++
        }
    }

    private companion object {
        const val SERVER_ERROR = 500
    }
}
