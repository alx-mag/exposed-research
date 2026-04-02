plugins {
    id("buildsrc.convention.spring")
    alias(libs.plugins.kotlinPluginJpa)
}

description = "spring-jpa"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}
