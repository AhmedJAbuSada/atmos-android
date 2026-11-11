// :data — talks to the outside world (network, database, settings) and implements the
// repository interfaces declared in :domain.
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kover)
}

android {
    namespace = "dev.ahmedabusada.atmos.data"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":domain"))

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
