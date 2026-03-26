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
