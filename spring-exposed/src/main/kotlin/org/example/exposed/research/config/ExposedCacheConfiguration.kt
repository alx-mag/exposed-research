package org.example.exposed.research.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

// Section 5.4 — enable Spring Cache for the exposed profile
@Configuration
@Profile("exposed")
@EnableCaching
class ExposedCacheConfiguration
