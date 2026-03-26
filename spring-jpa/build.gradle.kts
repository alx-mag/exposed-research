import buildsrc.Service
import buildsrc.recreateComposeService
import buildsrc.runK6
import org.gradle.kotlin.dsl.withType
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginJpa)
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "spring-jpa"

dependencies {
    implementation(project(":utils"))

    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.register<Exec>("deployContainer") {
    dependsOn("bootBuildImage")
    group = "deploy"
    description = "Build the spring-jpa image and recreate the spring-jpa container."
    recreateComposeService("spring-jpa")
}

tasks.register<Exec>("k6-GetUsers") {
    dependsOn(":prepareDb")
    runK6("get-test.js", Service.JPA)
}

tasks.register<Exec>("k6-GetUsersFiltering") {
    dependsOn(":prepareDb")
    runK6("get-filtering-test.js", Service.JPA)
}

tasks.register<Exec>("k6-LoadTest") {
    dependsOn(":prepareDb")
    runK6("load-test.js", Service.JPA)
}

tasks.withType<BootBuildImage> {
    imageName.set("alx-mag/${rootProject.name}-${project.name}")
}