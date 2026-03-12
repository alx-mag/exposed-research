package org.example.exposed.research.exposed

import org.jetbrains.exposed.v1.spring.boot4.autoconfigure.ExposedAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("exposed")
@ImportAutoConfiguration(ExposedAutoConfiguration::class)
class MyExposedConfiguration {
}