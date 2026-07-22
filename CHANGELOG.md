# Changelog

## [Unreleased]
### Added
- Unit tests covering Jackson (de)serialization
- Gradle version catalog (`gradle/libs.versions.toml`)

### Changed
- Java 25
- Kotlin 2.4.10
- Update to Gradle 9
- Migrate to Jackson 3 (`tools.jackson` coordinates)
- Bump Gradle plugins and dependencies
- Update CI actions (checkout, artifact, setup-gradle, git-release) and runner image
- Automate CHANGELOG release patching with the `org.jetbrains.changelog` plugin

### Fixed
- Release workflow authenticates via an SSH deploy key (`COMMIT_KEY`) instead of a stale PAT
- Release commit now actually stages `CHANGELOG.md` (axion's commit hook only commits explicitly registered patterns, so the changelog patch was previously silently dropped)

## [0.2.0] - 2022-11-27
### Added
- Separate publish and release creation action workflows

### Changed
- Java 17
- Updated Actions CI with newer actions, shared build job, and snapshot releases
- Update dependencies

### Fixed
- Use maven-compatible version for dependencies (jackson `2.14.0` instead of `2.14.+`)

## [0.1.0] - 2020-06-04
### Added
- Initial Release

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

[Unreleased]: https://github.com/dotRun/MCVotifierLib/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/dotRun/MCVotifierLib/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/dotRun/MCVotifierLib/releases/tag/v0.1.0
