// :domain — the business core. Pure Kotlin/JVM: no Android, no Retrofit, no Room.
// If you need to add a dependency here, ask first: it must not be a framework (ADR-0001).
plugins {
    alias(libs.plugins.kotlin.jvm)
    // Shares the in-memory fakes in src/testFixtures with other modules' tests:
    // `testImplementation(testFixtures(project(":domain")))`.
    `java-test-fixtures`
    alias(libs.plugins.kover)
}

kotlin {
    jvmToolchain(21)
}

// The domain holds the business rules, so it has the highest coverage bar (NF-05).
// `./gradlew :domain:koverVerify` fails the build below 90% line coverage.
kover {
    reports {
        verify {
            rule {
                minBound(90)
            }
        }
    }
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.javax.inject) // only the @Inject annotation, so Hilt can build use cases

    testFixturesImplementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
