package dev.ahmedabusada.atmos.data.testing

/**
 * Reads a recorded API response from docs/api/samples (added to the test resources in
 * data/build.gradle.kts). Tests use real responses, never hand-written JSON, so they
 * catch the API's real quirks.
 */
internal object Samples {
    fun read(name: String): String = requireNotNull(javaClass.classLoader?.getResource("samples/$name")) {
        "Sample $name not found in docs/api/samples"
    }.readText()
}
