package buildsrc.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

@DisableCachingByDefault(because = "Runs an external k6 process.")
abstract class K6ExecTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {

    init {
        group = "k6"
    }

    @get:Optional
    @get:Input
    abstract val scriptProperty: Property<String>

    @get:Input
    abstract val usersProperty: Property<String>

    @get:Input
    abstract val sleepMsProperty: Property<String>

    @get:Optional
    @get:Input
    abstract val baseUrlProperty: Property<String>

    @get:InputDirectory
    abstract val workingDirectory: DirectoryProperty

    @get:Internal
    var script: String?
        get() = scriptProperty.orNull
        set(value) = scriptProperty.set(value)

    @get:Internal
    var users: Int
        get() = usersProperty.get().toInt()
        set(value) = usersProperty.set(value.toString())

    @get:Internal
    var sleepMs: Int
        get() = sleepMsProperty.get().toInt()
        set(value) = sleepMsProperty.set(value.toString())

    @get:Internal
    var baseUrl: String?
        get() = baseUrlProperty.orNull
        set(value) = baseUrlProperty.set(value)

    private fun MutableList<String>.addEnv(name: String, value: String?) {
        if (!value.isNullOrBlank()) {
            add("-e")
            add("$name=$value")
        }
    }

    @TaskAction
    fun runK6() {
        val scriptName = scriptProperty.orNull ?: throw GradleException(
            "Missing required Gradle property 'script'. Example: -Pscript=get-test-local.js"
        )
        val command = mutableListOf<String>()
        command += listOf(
            "docker",
            "compose",
            "run",
            "--rm"
        )
        command.addEnv("USERS", usersProperty.orNull)
        command.addEnv("SLEEP_MS", sleepMsProperty.orNull)
        command.addEnv("BASE_URL", baseUrlProperty.orNull)
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
