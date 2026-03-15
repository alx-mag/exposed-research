package org.example.exposed.research.benchmark

import org.springframework.beans.factory.getBean
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.postgresql.PostgreSQLContainer

abstract class AbstractBenchmarkConfig {

    private val postgres: PostgreSQLContainer = PostgreSQLContainer("postgres:16-alpine").apply { start() }

    protected abstract val applicationClass: Class<*>
    protected open fun additionalProperties(): Map<String, String> = emptyMap()

    val context: ConfigurableApplicationContext by lazy {
        val app = SpringApplication(applicationClass)
        app.setDefaultProperties(
            mapOf(
                "spring.datasource.url" to postgres.jdbcUrl,
                "spring.datasource.username" to postgres.username,
                "spring.datasource.password" to postgres.password,
                "spring.datasource.driverClassName" to "org.postgresql.Driver",
            ) + additionalProperties()
        )
        app.setAdditionalProfiles("local")
        val ctx = app.run()
        Runtime.getRuntime().addShutdownHook(Thread {
            ctx.close()
            postgres.stop()
        })
        ctx
    }

    inline fun <reified T : Any> bean(): T = context.getBean<T>()
}
