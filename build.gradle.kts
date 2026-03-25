import Service.EXPOSED
import Service.JPA

group = "org.example"
version = "0.0.1-SNAPSHOT"

val prepareDb by tasks.register<Exec>("prepareDb") {
    description = "Execute db/clear-data.sql against the running db-service PostgreSQL container."
    val sqlFile = layout.projectDirectory.file("db/clear-data.sql").asFile
    doFirst {
        standardInput = sqlFile.inputStream()
    }
    commandLine(
        "docker",
        "exec",
        "-i",
        "db-service",
        "psql",
        "-U",
        "postgres",
        "-d",
        "postgres"
    )
}

tasks.register<Exec>("deploySpringExposed") {
    dependsOn(":spring-exposed:bootBuildImage")
    group = "deploy"
    description = "Build the spring-exposed image and recreate the spring-exposed container."
    recreateComposeService("spring-exposed")
}

tasks.register<Exec>("deploySpringJpa") {
    dependsOn(":spring-jpa:bootBuildImage")
    group = "deploy"
    description = "Build the spring-jpa image and recreate the spring-exposed container."
    recreateComposeService("spring-jpa")
}

fun Exec.recreateComposeService(service: String) {
    commandLine(
        "docker",
        "compose",
        "-f",
        "deployment/docker-compose.yaml",
        "up",
        "-d",
        "--force-recreate",
        "--no-deps",
        service
    )
}

/// Exposed ///
tasks.register<Exec>("k6-GetUsers-Exposed") {
    dependsOn(prepareDb)
    runK6("get-test.js", EXPOSED)
}

tasks.register<Exec>("k6-GetUsersFiltering-Exposed") {
    dependsOn(prepareDb)
    runK6("get-filtering-test.js", EXPOSED)
}

tasks.register<Exec>("k6-LoadTest-Exposed") {
    dependsOn(prepareDb)
    runK6("load-test.js", EXPOSED)
}

/// JPA ///
tasks.register<Exec>("k6GetUsersJpa") {
    dependsOn(prepareDb)
    runK6("get-test.js", JPA)
}

tasks.register<Exec>("k6GetUsersFilteringJpa") {
    dependsOn(prepareDb)
    runK6("get-filtering-test.js", JPA)
}

tasks.register<Exec>("k6-LoadTest-JPA") {
    dependsOn(prepareDb)
    runK6("load-test.js", JPA)
}

/// Common ///
fun Exec.runK6(scriptName: String, baseUrl: String) {
    fun MutableList<String>.addEnv(name: String, value: Any) {
        add("-e")
        add("$name=$value")
    }

    workingDir = layout.projectDirectory.dir("k6-testing").asFile
    val command = mutableListOf<String>()
    command += listOf(
        "docker",
        "compose",
        "run",
        "--rm"
    )
    command.addEnv("USERS", 20)
    command.addEnv("SLEEP_MS", 50)
    command.addEnv("BASE_URL", baseUrl)
    command += listOf(
        "k6",
        "run",
        "/k6-scripts/$scriptName"
    )
    commandLine = command
}

object Service {
    const val EXPOSED = "http://nginx:4000/spring-exposed"
    const val JPA = "http://nginx:4000/spring-jpa"

    const val EXPOSED_LOCAL = "http://localhost:9081"
    const val JPA_LOCAL = "http://localhost:9082"
}
