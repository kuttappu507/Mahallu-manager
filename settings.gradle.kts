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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "MahalluManager"

include(":app")

include(":core")
include(":core-ui")
include(":core-database")
include(":core-security")
include(":core-network")

include(":feature-auth")
include(":feature-dashboard")
include(":feature-families")
include(":feature-members")
include(":feature-subscriptions")
include(":feature-donations")
include(":feature-finance")
include(":feature-marriage")
include(":feature-death")
include(":feature-welfare")
include(":feature-certificates")
include(":feature-reports")
include(":feature-settings")
include(":feature-search")