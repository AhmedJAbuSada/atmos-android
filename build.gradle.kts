// Root build file: declares plugin versions once (apply false) and sets up the
// checks that run on every module. Module-specific setup lives in each module.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.spotless)
}

// Formatting: ktlint through Spotless. `./gradlew spotlessApply` fixes everything.
spotless {
    val ktlintVersion = libs.versions.ktlint.get()
    kotlin {
        target("**/src/**/*.kt")
        targetExclude("**/build/**")
        ktlint(ktlintVersion).editorConfigOverride(
            mapOf(
                // Compose functions are PascalCase by convention.
                "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
            ),
        )
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**")
        ktlint(ktlintVersion)
    }
}

// Locale-safe text casing (ADR-0005). Kotlin's no-argument uppercase()/lowercase()
// use Locale.ROOT, so "istanbul".uppercase() gives "ISTANBUL", not "İSTANBUL", and the
// Java toUpperCase()/toLowerCase() use the device locale, which breaks identifiers on
// Turkish devices. Always pass a Locale explicitly. This small task makes it a build
// error instead of a code-review comment.
val checkLocaleSafeCasing =
    tasks.register("checkLocaleSafeCasing") {
        group = "verification"
        description = "Fails if text casing is changed without an explicit Locale."
        val root = rootDir
        val sources =
            fileTree(root) {
                include("**/src/main/**/*.kt")
                exclude("**/build/**")
            }
        inputs.files(sources)
        doLast {
            val forbidden =
                Regex(
                    """\.(uppercase|lowercase|toUpperCase|toLowerCase)\(\s*\)|\.(capitalize|decapitalize)\(""",
                )
            val violations =
                sources.files.flatMap { file ->
                    file.readLines().mapIndexedNotNull { index, line ->
                        if (forbidden.containsMatchIn(line)) {
                            "${file.relativeTo(root)}:${index + 1}: ${line.trim()}"
                        } else {
                            null
                        }
                    }
                }
            if (violations.isNotEmpty()) {
                throw GradleException(
                    "Pass an explicit Locale when changing text case (see ADR-0005):\n" +
                        violations.joinToString("\n"),
                )
            }
        }
    }

subprojects {
    tasks.matching { it.name == "check" }.configureEach {
        dependsOn(checkLocaleSafeCasing)
    }
}
