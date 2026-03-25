import org.gradle.api.tasks.Exec

group = "org.example"
version = "0.0.1-SNAPSHOT"

object Service {
    const val EXPOSED = "http://nginx:4000/spring-exposed"
    const val JPA =     "http://nginx:4000/spring-jpa"

    const val EXPOSED_LOCAL = "http://localhost:9081"
    const val JPA_LOCAL =     "http://localhost:9082"
}

val k6TestingDir = layout.projectDirectory.dir("k6-testing")

val k6Script = providers.gradleProperty("script")
val k6Type = providers.gradleProperty("type")
val k6Users = providers.gradleProperty("users").orElse("20")
val k6SleepMs = providers.gradleProperty("sleepMs").orElse("0")
val k6Host = providers.gradleProperty("host")
val k6Port = providers.gradleProperty("port")
val k6BaseUrl = providers.gradleProperty("baseUrl")


fun MutableList<String>.addEnv(name: String, value: String?) {
    if (!value.isNullOrBlank()) {
        add("-e")
        add("$name=$value")
    }
}

fun Exec.configureK6ContainerRun2(
    scriptProvider: Provider<String>,
    baseUrlProvider: Provider<String>,
    usersProvider: Provider<String> = k6Users,
    sleepMsProvider: Provider<String> = k6SleepMs
) {
    group = "k6"
    workingDir = k6TestingDir.asFile

    val command = mutableListOf(
        "docker", "compose", "run", "--rm"
    )
    command.addEnv("BASE_URL", baseUrlProvider.orNull)
    command.addEnv("USERS", usersProvider.orNull)
    command.addEnv("SLEEP_MS", sleepMsProvider.orNull)
    command += listOf(
        "k6",
        "run",
        "/k6-scripts/${scriptProvider.get()}"
    )
    commandLine(command)
}

fun Exec.configureK6ContainerRun(
    scriptProvider: Provider<String>,
    typeProvider: Provider<String>,
    usersProvider: Provider<String>,
    sleepMsProvider: Provider<String>,
    hostProvider: Provider<String>,
    portProvider: Provider<String>,
    baseUrlProvider: Provider<String>
) {
    group = "k6"
    workingDir = k6TestingDir.asFile

    val command = mutableListOf(
        "docker", "compose", "run", "--rm"
    )
    command.addEnv("TYPE", typeProvider.orNull)
    command.addEnv("USERS", usersProvider.orNull)
    command.addEnv("SLEEP_MS", sleepMsProvider.orNull)
    command.addEnv("HOST", hostProvider.orNull)
    command.addEnv("PORT", portProvider.orNull)
    command.addEnv("BASE_URL", baseUrlProvider.orNull)
    command += listOf(
        "k6",
        "run",
        "/k6-scripts/${scriptProvider.get()}"
    )
    commandLine(command)
}

fun Exec.configureK6LocalRun(
    scriptProvider: Provider<String>,
    usersProvider: Provider<String>,
    sleepMsProvider: Provider<String>,
    hostProvider: Provider<String>,
    portProvider: Provider<String>,
    baseUrlProvider: Provider<String>
) {
    group = "k6"
    workingDir = k6TestingDir.asFile

    val command = mutableListOf(
        "k6",
        "run",
        "./js/${scriptProvider.get()}",
        "-e", "USERS=${usersProvider.get()}"
    )
    sleepMsProvider.orNull?.takeIf { it.isNotBlank() }?.let {
        command += listOf("-e", "SLEEP_MS=$it")
    }
    hostProvider.orNull?.takeIf { it.isNotBlank() }?.let {
        command += listOf("-e", "HOST=$it")
    }
    portProvider.orNull?.takeIf { it.isNotBlank() }?.let {
        command += listOf("-e", "PORT=$it")
    }
    baseUrlProvider.orNull?.takeIf { it.isNotBlank() }?.let {
        command += listOf("-e", "BASE_URL=$it")
    }
    commandLine(command)
}


tasks.register<Exec>("k6Run") {
    description =
        "Run a k6 script in Docker Compose. Example: ./gradlew k6Run -Pscript=get-filtering-test.js -Ptype=jpa -Pusers=20"
    configureK6ContainerRun(
        scriptProvider = k6Script,
        typeProvider = k6Type,
        usersProvider = k6Users,
        sleepMsProvider = k6SleepMs,
        hostProvider = k6Host,
        portProvider = k6Port,
        baseUrlProvider = k6BaseUrl
    )
}

tasks.register<Exec>("k6RunLocal") {
    description =
        "Run a local k6 script. Example: ./gradlew k6RunLocal -Pscript=get-test-local.js -Pusers=20 -Pport=9082"
    configureK6LocalRun(
        scriptProvider = k6Script,
        usersProvider = k6Users,
        sleepMsProvider = k6SleepMs,
        hostProvider = k6Host,
        portProvider = k6Port,
        baseUrlProvider = k6BaseUrl
    )
}

tasks.register<Exec>("k6ExposedGetUsers") {
    description = "Run the containerized Exposed get-users test."
    configureK6ContainerRun2(
        providers.provider { "get-test.js" },
        providers.provider { Service.EXPOSED },
    )
}

tasks.register<Exec>("k6JpaGetUsers") {
    description = "Run the containerized JPA get-users test."
    configureK6ContainerRun2(
        providers.provider { "get-test.js" },
        providers.provider { Service.JPA },
    )
}

tasks.register<Exec>("k6JpaFiltering") {
    description = "Run the containerized JPA filtering test."
    configureK6ContainerRun2(
        providers.provider { "get-filtering-test.js" },
        providers.provider { Service.EXPOSED }
    )
}

tasks.register<Exec>("k6ExposedFiltering") {
    description = "Run the containerized Exposed filtering test."
    configureK6ContainerRun(
        scriptProvider = providers.provider { "get-filtering-test.js" },
        typeProvider = providers.provider { "exposed" },
        usersProvider = k6Users,
        sleepMsProvider = k6SleepMs,
        hostProvider = providers.provider { null },
        portProvider = providers.provider { null },
        baseUrlProvider = providers.provider { null }
    )
}
