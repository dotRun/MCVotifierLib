import org.jetbrains.changelog.date
import org.jetbrains.changelog.tasks.PatchChangelogTask
import pl.allegro.tech.build.axion.release.domain.hooks.HookContext

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
    alias(libs.plugins.axion.release)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.changelog)
}

group = "io.dotrun"
version = scmVersion.version

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

scmVersion {
    versionIncrementer("incrementMinorIfNotOnRelease", mapOf("releaseBranchPattern" to "release/.+"))

    hooks {
        pre { context: HookContext ->
            patchChangelogToVersion(context.releaseVersion)
            context.addCommitPattern("CHANGELOG.md")
        }
        pre("commit")
    }
}

changelog {
    repositoryUrl.set("https://github.com/dotRun/MCVotifierLib")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
}

fun patchChangelogToVersion(newVersion: String) {
    tasks
        .named<PatchChangelogTask>("patchChangelog")
        .get()
        .apply {
            version.set(newVersion)
            header.set("$newVersion - ${date()}")
        }.run()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.jackson.module.kotlin)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/dotRun/MCVotifierLib")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

tasks {
    wrapper {
        gradleVersion = "9.6.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}
