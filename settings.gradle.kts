pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MahalluManager"
include(":app")
include(":core:database")
include(":core:security")
include(":core:ui")
include(":feature:auth")
include(":feature:dashboard")
include(":feature:families")
include(":feature:members")
include(":feature:subscriptions")
include(":feature:donations")
include(":feature:finance")
include(":feature:marriage")
include(":feature:death")
include(":feature:welfare")
include(":feature:certificates")
include(":feature:reports")
include(":feature:settings")
