package buildsrc

import org.gradle.api.tasks.Exec

object Service {
    const val EXPOSED = "http://nginx:4000/spring-exposed"
    const val JPA = "http://nginx:4000/spring-jpa"

}

fun Exec.runK6(scriptName: String, baseUrl: String) {
    fun MutableList<String>.addEnv(name: String, value: Any) {
        add("-e")
        add("$name=$value")
    }

    workingDir = project.rootProject.layout.projectDirectory.dir("k6-testing").asFile
    val command = mutableListOf<String>()
    command += listOf(
        "docker",
        "compose",
        "run",
        "--rm"
    )
    command.addEnv("USERS", 20)
    command.addEnv("SLEEP_MS", 0)
    command.addEnv("BASE_URL", baseUrl)
    command += listOf(
        "k6",
        "run",
        "/k6-scripts/$scriptName"
    )
    commandLine = command
}

fun Exec.recreateComposeService(service: String) {
    commandLine(
        "docker",
        "compose",
        "-f",
        project.rootProject.layout.projectDirectory.file("deployment/docker-compose.yaml").asFile.absolutePath,
        "up",
        "-d",
        "--force-recreate",
        "--no-deps",
        service
    )
}
