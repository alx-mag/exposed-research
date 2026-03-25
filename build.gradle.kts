import buildsrc.tasks.K6ExecTask

group = "org.example"
version = "0.0.1-SNAPSHOT"

object Service {
    const val EXPOSED = "http://nginx:4000/spring-exposed"
    const val JPA =     "http://nginx:4000/spring-jpa"

    const val EXPOSED_LOCAL = "http://localhost:9081"
    const val JPA_LOCAL =     "http://localhost:9082"
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
