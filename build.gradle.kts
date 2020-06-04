import pl.allegro.tech.build.axion.release.domain.hooks.HookContext
import pl.allegro.tech.build.axion.release.domain.hooks.HooksConfig
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm") version "1.3.72"
    id("maven-publish")
    id("pl.allegro.tech.build.axion-release") version "1.12.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

val repoRef = "dotRun\\/MCVotifierLib"

group = "io.dotrun"
version = scmVersion.version

scmVersion {
    hooks(closureOf<HooksConfig> {

        // "normal" changelog update--changelog already contains a history
        pre(
            "fileUpdate",
            mapOf(
                "file" to "CHANGELOG.md",
                "pattern" to KotlinClosure2<String, HookContext, String>({ v, _ ->
                    "\\[Unreleased\\]([\\s\\S]+?)\\n(?:^\\[Unreleased\\]: https:\\/\\/github\\.com\\/$repoRef\\/compare\\/release-$v\\.\\.\\.HEAD\$([\\s\\S]*))?\\z"
                }),
                "replacement" to KotlinClosure2<String, HookContext, String>({ v, c ->
                    """
                        \[Unreleased\]
                        
                        ## \[$v\] - ${currentDateString()}$1
                        \[Unreleased\]: https:\/\/github\.com\/$repoRef\/compare\/release-$v...HEAD
                        \[$v\]: https:\/\/github\.com\/$repoRef\/compare\/release-${c.previousVersion}...release-$v$2
                    """.trimIndent()
                })
            )
        )
        // first-time changelog update--changelog has only unreleased info
        pre(
            "fileUpdate",
            mapOf(
                "file" to "CHANGELOG.md",
                "pattern" to KotlinClosure2<String, HookContext, String>({ v, _ ->
                    "Unreleased([\\s\\S]+?\\nand this project adheres to \\[Semantic Versioning\\]\\(https:\\/\\/semver\\.org\\/spec\\/v2\\.0\\.0\\.html\\).)\\s\\z"
                }),
                "replacement" to KotlinClosure2<String, HookContext, String>({ v, c ->
                    """
                        \[Unreleased\]
                        
                        ## \[$v\] - ${currentDateString()}$1
                        
                        \[Unreleased\]: https:\/\/github\.com\/$repoRef\/compare\/release-$v...HEAD
                        \[$v\]: https:\/\/github\.com\/$repoRef\/releases\/tag\/release-$v
                    """.trimIndent()
                })
            )
        )
        pre("commit")
    })
}

fun currentDateString() = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().format(DateTimeFormatter.ISO_DATE)

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = "2.11.+")
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
        gradleVersion = "6.5"
        distributionType = Wrapper.DistributionType.ALL
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}
