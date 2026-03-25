import buildsrc.tasks.K6ExecTask

group = "org.example"
version = "0.0.1-SNAPSHOT"

object Service {
    const val EXPOSED = "http://nginx:4000/spring-exposed"
    const val JPA =     "http://nginx:4000/spring-jpa"

    const val EXPOSED_LOCAL = "http://localhost:9081"
    const val JPA_LOCAL =     "http://localhost:9082"
}

val k6TestingDir = layout.projectDirectory.dir("k6-testing")

tasks.register<K6ExecTask>("k6ExposedGetUsers") {
    description = "Run the containerized Exposed get-users test."
    workingDirectory.set(k6TestingDir)
    script = "get-test.js"
    users = 20
    sleepMs = 0
    baseUrl = Service.EXPOSED
}

tasks.register<K6ExecTask>("k6ExposedFiltering") {
    description = "Run the containerized Exposed filtering test."
    workingDirectory.set(k6TestingDir)
    script = "get-filtering-test.js"
    users = 20
    sleepMs = 0
    baseUrl = Service.EXPOSED
}

tasks.register<K6ExecTask>("k6JpaGetUsers") {
    description = "Run the containerized JPA get-users test."
    workingDirectory.set(k6TestingDir)
    script = "get-test.js"
    users = 20
    sleepMs = 0
    baseUrl = Service.JPA
}

tasks.register<K6ExecTask>("k6JpaFiltering") {
    description = "Run the containerized JPA filtering test."
    workingDirectory.set(k6TestingDir)
    script = "get-filtering-test.js"
    users = 20
    sleepMs = 0
    baseUrl = Service.JPA
}

