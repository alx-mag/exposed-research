import Service.EXPOSED
import Service.JPA

group = "org.example"
version = "0.0.1-SNAPSHOT"

tasks.register<Exec>("k6GetUsersExp") {
    description = "Run the containerized Exposed get-users test."
    runK6("get-test.js", EXPOSED)
}

tasks.register<Exec>("k6GetUsersFilteringExp") {
    description = "Run the containerized Exposed filtering test."
    runK6("get-filtering-test.js", EXPOSED)
}

tasks.register<Exec>("k6LoadTest-EXPOSED") {
    runK6("load-test.js", EXPOSED)
}

tasks.register<Exec>("k6GetUsersJpa") {
    description = "Run the containerized JPA get-users test."
    runK6("get-test.js", JPA)
}

tasks.register<Exec>("k6GetUsersFilteringJpa") {
    description = "Run the containerized JPA filtering test."
    runK6("get-filtering-test.js", JPA)
}

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
