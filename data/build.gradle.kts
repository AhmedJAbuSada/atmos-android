// :data — talks to the outside world (network, database, settings, location) and
// implements the repository interfaces declared in :domain.
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
    alias(libs.plugins.kover)
}

android {
    namespace = "dev.ahmedabusada.atmos.data"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    sourceSets {
        // Tests read the real recorded API responses from docs/api/samples, the same
        // files the API contract links to. One source of truth: if the contract's
        // samples change, the tests see it.
        getByName("test").resources.directories.add("$rootDir/docs/api")
    }

    testOptions {
        // Robolectric needs Android resources to run Room on the JVM.
        unitTests.isIncludeAndroidResources = true
    }
}

kotlin {
    jvmToolchain(21)
}

room {
    // Exported schemas are committed, so every database change is visible in review
    // and migrations can be tested against the old schema.
    schemaDirectory("$projectDir/schemas")
}

// The data layer is the boundary with the outside world; it must be well tested (NF-05).
kover {
    reports {
        filters {
            excludes {
                // Generated code (Hilt, Room) and DI wiring are not ours to test.
                classes("*_Factory", "*_Impl", "*_Impl\$*", "*Hilt*", "*_MembersInjector", "dagger.*")
                packages("dev.ahmedabusada.atmos.data.di")
            }
        }
        verify {
            rule {
                minBound(80)
            }
        }
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.datastore.preferences)

    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.okhttp.mockwebserver)
}
