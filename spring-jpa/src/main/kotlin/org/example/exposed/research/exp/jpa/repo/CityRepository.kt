package org.example.exposed.research.exp.jpa.repo

import org.example.exposed.research.exp.jpa.entity.City
import org.springframework.data.jpa.repository.JpaRepository

// Section 4.1 — JPA City repository
interface CityRepository : JpaRepository<City, Int>
