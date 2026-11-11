// :domain — the business core. Pure Kotlin/JVM: no Android, no Retrofit, no Room.
// If you need to add a dependency here, ask first: it must not be a framework (ADR-0001).
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kover)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.javax.inject) // only the @Inject annotation, so Hilt can build use cases

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
