package org.example.exposed.research.benchmark

import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.postgresql.PostgreSQLContainer

object BenchmarkConfig {

    private val postgres: PostgreSQLContainer = PostgreSQLContainer("postgres:16-alpine")
        .apply { start() }

    val context: ConfigurableApplicationContext by lazy {
        val app = SpringApplication(org.example.exposed.research.SpringExposedApplication::class.java)
        app.setDefaultProperties(
            mapOf(
                "spring.datasource.url" to postgres.jdbcUrl,
                "spring.datasource.username" to postgres.username,
                "spring.datasource.password" to postgres.password,
                "spring.datasource.driverClassName" to "org.postgresql.Driver",
                "spring.jpa.hibernate.ddl-auto" to "none",
                "spring.exposed.generate-ddl" to "true",
                "spring.exposed.show-sql" to "false",
            )
        )
        app.setAdditionalProfiles("local")
        val ctx = app.run()
        Runtime.getRuntime().addShutdownHook(Thread {
            ctx.close()
            postgres.stop()
        })
        ctx
    }

    inline fun <reified T : Any> bean(): T = context.getBean(T::class.java)
}
