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

val k6Script = providers.gradleProperty("script")
val k6Users = providers.gradleProperty("users").orElse("20")
val k6SleepMs = providers.gradleProperty("sleepMs").orElse("0")
val k6BaseUrl = providers.gradleProperty("baseUrl")

tasks.register<K6ExecTask>("k6ExposedGetUsers") {
    group = "k6"
    description = "Run the containerized Exposed get-users test."
    workingDirectory.set(k6TestingDir)
    script.convention("get-test.js")
    users.set(k6Users)
    sleepMs.set(k6SleepMs)
    baseUrl.set(Service.EXPOSED)
}

tasks.register<K6ExecTask>("k6ExposedFiltering") {
    group = "k6"
    description = "Run the containerized Exposed filtering test."
    workingDirectory.set(k6TestingDir)
    script.convention("get-filtering-test.js")
    users.set(k6Users)
    sleepMs.set(k6SleepMs)
    baseUrl.set(Service.EXPOSED)
}

tasks.register<K6ExecTask>("k6JpaGetUsers") {
    group = "k6"
    description = "Run the containerized JPA get-users test."
    workingDirectory.set(k6TestingDir)
    script.convention("get-test.js")
    users.set(k6Users)
    sleepMs.set(k6SleepMs)
    baseUrl.set(Service.JPA)
}

tasks.register<K6ExecTask>("k6JpaFiltering") {
    group = "k6"
    description = "Run the containerized JPA filtering test."
    workingDirectory.set(k6TestingDir)
    script.convention("get-filtering-test.js")
    users.set(k6Users)
    sleepMs.set(k6SleepMs)
    baseUrl.set(Service.JPA)
}

