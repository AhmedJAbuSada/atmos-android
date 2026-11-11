// :app — presentation layer (Compose UI, ViewModels) and the composition root, where
// Hilt wires :domain and :data together. It is the only module that sees all layers.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization) // type-safe navigation routes
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kover)
}

android {
    namespace = "dev.ahmedabusada.atmos"
    compileSdk = 37

    defaultConfig {
        applicationId = "dev.ahmedabusada.atmos"
        minSdk = 26
        targetSdk = 37
        versionCode = 10
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Release signing comes from environment variables (CI secrets or a developer's shell),
    // never from a file in the repository. Without them, the release build is unsigned,
    // which is fine for checking that it builds and shrinks correctly.
    val releaseKeystore = System.getenv("ATMOS_KEYSTORE_PATH")
    signingConfigs {
        if (releaseKeystore != null) {
            create("release") {
                storeFile = file(releaseKeystore)
                storePassword = System.getenv("ATMOS_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("ATMOS_KEY_ALIAS")
                keyPassword = System.getenv("ATMOS_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            // Pseudo-locales: en-XA stretches every string (~30% longer, with accents) and
            // ar-XB mirrors the layout, so truncation and RTL bugs show up without a
            // translator (Settings → Languages on the device).
            isPseudoLocalesEnabled = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            // Screenshot tests render real Compose UI on the JVM with Robolectric.
            isIncludeAndroidResources = true
            all { test ->
                // Robolectric needs this JDK 17+ package opened (see data/build.gradle.kts).
                test.jvmArgs("--add-opens=java.base/jdk.internal.access=ALL-UNNAMED")
                // `./gradlew :app:testDebugUnitTest -PrecordScreenshots` writes the images
                // to docs/screenshots; a normal test run renders but writes nothing.
                test.systemProperty("roborazzi.test.record", project.hasProperty("recordScreenshots").toString())
                test.systemProperty("atmos.screenshots.dir", "$rootDir/docs/screenshots")
            }
        }
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
        checkDependencies = true
        // New dependency versions appear every week; they are reviewed on purpose,
        // not as a build failure.
        disable += setOf("GradleDependency", "NewerVersionAvailable", "AndroidGradlePluginVersion")
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.appcompat) // per-app language on Android 8–12 (ADR-0005)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    debugImplementation(libs.compose.ui.test.manifest)

    testImplementation(testFixtures(project(":domain")))
    testImplementation(platform(libs.compose.bom))
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}

// Kover: one coverage report for all modules (`./gradlew koverHtmlReport`).
dependencies {
    kover(project(":domain"))
    kover(project(":data"))
}
