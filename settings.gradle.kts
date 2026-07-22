rootProject.name = "mc-votifier-lib"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    // Version catalog aliases aren't resolvable from the settings script itself, so this is pinned directly.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
