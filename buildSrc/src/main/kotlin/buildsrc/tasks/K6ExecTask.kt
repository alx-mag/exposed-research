package buildsrc.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault(because = "Runs an external k6 process.")
abstract class K6ExecTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {
    @get:Optional
    @get:Input
    abstract val script: Property<String>

    @get:Input
    abstract val users: Property<String>

    @get:Input
    abstract val sleepMs: Property<String>

    @get:Optional
    @get:Input
    abstract val baseUrl: Property<String>

    @get:InputDirectory
    abstract val workingDirectory: DirectoryProperty

    private fun MutableList<String>.addEnv(name: String, value: String?) {
        if (!value.isNullOrBlank()) {
            add("-e")
            add("$name=$value")
        }
    }

    @TaskAction
    fun runK6() {
        val scriptName = script.orNull ?: throw GradleException(
            "Missing required Gradle property 'script'. Example: -Pscript=get-test-local.js"
        )
        val command = mutableListOf<String>()
        command += listOf(
            "docker",
            "compose",
            "run",
            "--rm"
        )
        command.addEnv("USERS", users.orNull)
        command.addEnv("SLEEP_MS", sleepMs.orNull)
        command.addEnv("BASE_URL", baseUrl.orNull)
        command += listOf(
            "k6",
            "run",
            "/k6-scripts/$scriptName"
        )
        execOperations.exec {
            workingDir(workingDirectory)
            commandLine(command)
        }
    }
}
