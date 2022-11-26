import pl.allegro.tech.build.axion.release.domain.hooks.HookContext
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm") version "1.7.21"
    id("maven-publish")
    id("pl.allegro.tech.build.axion-release") version "1.14.2"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

val repoRef = "dotRun\\/MCVotifierLib"

group = "io.dotrun"
version = scmVersion.version

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

scmVersion {
    versionIncrementer("incrementMinorIfNotOnRelease", mapOf("releaseBranchPattern" to "release/.+"))

    hooks {
        // FIXME - workaround for Kotlin DSL issue https://github.com/allegro/axion-release-plugin/issues/500
        pre(
            "fileUpdate",
            mapOf(
                "file" to "CHANGELOG.md",
                "pattern" to KotlinClosure2<String, HookContext, String>({ _, _ ->
                    "\\[Unreleased\\]([\\s\\S]+?)\\n(?:^\\[Unreleased\\]: https:\\/\\/github\\.com\\/$repoRef\\/compare\\/[^\\n]*\$([\\s\\S]*))?\\z"
                }),
                "replacement" to KotlinClosure2<String, HookContext, String>({ v, c ->
                    """
                        \[Unreleased\]
                        
                        ## \[$v\] - ${currentDateString()}$1
                        \[Unreleased\]: https:\/\/github\.com\/$repoRef\/compare\/v$v...HEAD
                        \[$v\]: https:\/\/github\.com\/$repoRef\/${if (c.previousVersion == v) "releases/tag/v$v" else "compare/v${c.previousVersion}...v$v"}${'$'}2
                    """.trimIndent()
                })
            )
        )
    }
}

fun currentDateString() = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().format(DateTimeFormatter.ISO_DATE)

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = "2.14.0")
}

ktlint {
    // FIXME - ktlint bug(?): https://github.com/pinterest/ktlint/issues/527
    disabledRules.set(listOf("import-ordering"))
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
        gradleVersion = "7.6"
        distributionType = Wrapper.DistributionType.ALL
    }
}
