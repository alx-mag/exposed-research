import buildsrc.tasks.K6ExecTask

group = "org.example"
version = "0.0.1-SNAPSHOT"

object Service {
    const val EXPOSED = "http://nginx:4000/spring-exposed"
    const val JPA = "http://nginx:4000/spring-jpa"

    const val EXPOSED_LOCAL = "http://localhost:9081"
    const val JPA_LOCAL = "http://localhost:9082"
}

tasks.register<K6ExecTask>("k6GetUsersExp") {
    description = "Run the containerized Exposed get-users test."
    script = "get-test.js"
    users = 20
    sleepMs = 0
    baseUrl = Service.EXPOSED
}

tasks.register<K6ExecTask>("k6GetUsersFilteringExp") {
    description = "Run the containerized Exposed filtering test."
    script = "get-filtering-test.js"
    users = 20
    sleepMs = 0
    baseUrl = Service.EXPOSED
}

tasks.register<K6ExecTask>("k6GetUsersJpa") {
    description = "Run the containerized JPA get-users test."
    script = "get-test.js"
    users = 20
    sleepMs = 0
    baseUrl = Service.JPA
}

tasks.register<K6ExecTask>("k6GetUsersFilteringJpa") {
    description = "Run the containerized JPA filtering test."
    script = "get-filtering-test.js"
    users = 20
    sleepMs = 0
    baseUrl = Service.JPA
}

tasks.register<Exec>("a") {
    runScript("get-filtering-test.js", Service.JPA)
}

fun Exec.runScript(scriptName: String, baseUrl: String) {
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
