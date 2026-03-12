package org.example.exposed.research.jpa

import org.springframework.data.jpa.repository.JpaRepository

// Section 4.1 — JPA City repository
interface CityRepository : JpaRepository<City, Int>
