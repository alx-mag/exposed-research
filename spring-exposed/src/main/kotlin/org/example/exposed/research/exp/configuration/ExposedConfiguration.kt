package org.example.exposed.research.exp.configuration

import org.jetbrains.exposed.v1.core.DatabaseConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ExposedConfiguration {

    @Bean
    fun databaseConfig() = DatabaseConfig {
        // keepLoadedReferencesOutOfTransaction = true
    }
}
