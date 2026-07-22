# Changelog

## [Unreleased]

## [0.3.0] - 2026-07-22

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
- Replace the `antonyurchenko/git-release` Docker action with `gh release create`/`gh release delete`, using the `org.jetbrains.changelog` plugin's `getChangelog` task to extract release notes instead of a separate third-party changelog parser

### Fixed

- Release workflow authenticates via an SSH deploy key (`COMMIT_KEY`) instead of a stale PAT
- Release commit now actually stages `CHANGELOG.md` (axion's commit hook only commits explicitly registered patterns, so the changelog patch was previously silently dropped)
- Changelog plugin no longer pre-populates the new Unreleased section with empty Keep a Changelog group headers
- Release hook now fails loudly (instead of silently skipping the whole release) if the Unreleased section has no content when a release runs
- Publish workflow's registry publish step is skipped on the branch-triggered run when it's also sitting on a release tag, avoiding a duplicate-version 409 against the tag-triggered run

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

[Unreleased]: https://github.com/dotRun/MCVotifierLib/compare/v0.3.0...HEAD
[0.3.0]: https://github.com/dotRun/MCVotifierLib/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/dotRun/MCVotifierLib/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/dotRun/MCVotifierLib/commits/v0.1.0
