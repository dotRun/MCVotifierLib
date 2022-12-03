# MCVotifierLib
Votifier client library for server lists written in Kotlin

## Features
- Votifier protocol V1 (legacy)
- Votifier protocol V2 ([nuVotifier](https://github.com/NuVotifier/NuVotifier/wiki/Technical-QA#protocol-v2))

## Usage

MCVotifierLib is [published to Github Packages](https://github.com/dotRun/MCVotifierLib/packages/254381) for usage in Maven or Gradle.

Github Packages' Maven registry [requires authentication](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages), so it is recommended to generate a Personal Access Token with `packages:read` scope.

### Gradle

See [Authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages) for more information on authenticating to GitHub Package Maven registry. Below is an example `build.gradle.kts` utilizing the `net.saliman.properties` plugin

```kotlin
plugins {
    // ...
    id("net.saliman.properties") version "1.5.2"
}

repositories {
    mavenCentral()
    // ...other repositories
    maven("https://maven.pkg.github.com/dotRun/MCVotifierLib") {
        name = "GithubPackages"
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_KEY")
        }
    }
}

// ...
```

## API

The MCVotifierLib library is very small. In order to use it you must first construct a [`VoteSender`](src/main/kotlin/io/dotrun/mcvotifierlib/VoteSender.kt) ([V1](src/main/kotlin/io/dotrun/mcvotifierlib/V1VoteSender.kt) or [V2](src/main/kotlin/io/dotrun/mcvotifierlib/V2VoteSender.kt)) for a given votifier server. That `VoteSender` can then be used to send [`Vote`](src/main/kotlin/io/dotrun/mcvotifierlib/Vote.kt)s.

### Example Usage
Here's an example of using the library running on the [kotlin-interactive-shell](https://github.com/Kotlin/kotlin-interactive-shell). It assumes a locally-running Minecraft server using Votifier V2 on port 8192
```kotlin
❯ ki
ki-shell 0.5.2/1.7.0
type :h for help
[0] :repository https://maven.pkg.github.com/dotRun/MCVotifierLib mc-votifier-lib-repo.properties
[1] :dependsOn io.dotrun:mc-votifier-lib:0.2.0
[2] import java.net.InetSocketAddress
[3] import io.dotrun.mcvotifierlib.V2VoteSender
[4] import io.dotrun.mcvotifierlib.Vote
[5] val address = InetSocketAddress("localhost", 8192) // server address -- NOTE This is the votifier port, NOT the Minecraft port
[6] val token = "<*sensitive*>" // v2 votifier token
[7] val voteSender = V2VoteSender(address, token)
[8] val vote = Vote(serviceName = "test-service", username = "testuser", address = "127.0.0.1")
[9] voteSender.sendVote(vote)
```
This results in the following log line on the server, showing that the vote was received:
```
[21:08:48 INFO]: [Votifier] Got a protocol v2 vote record from /172.17.0.1:54098 -> Vote (from:test-service username:testuser address:127.0.0.1 timeStamp:1670101716542 additionalData:null)
```
