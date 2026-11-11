pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // Modules may not declare their own repositories: one source of truth.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "atmos-android"

// Clean Architecture layers. The dependency rule (ADR-0001):
//   :app    → :domain, :data   (presentation + composition root)
//   :data   → :domain          (implements the domain's interfaces)
//   :domain → nothing          (pure Kotlin)
include(":app")
include(":data")
include(":domain")
