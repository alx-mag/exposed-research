import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("buildsrc.convention.kotlin-jvm")
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "spring-exposed"

dependencies {
    implementation(project(":utils"))

    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")

    implementation(platform("org.jetbrains.exposed:exposed-bom:1.1.1"))
    implementation("org.jetbrains.exposed:exposed-spring-boot4-starter")
    implementation("org.jetbrains.exposed:exposed-dao")
    implementation("org.jetbrains.exposed:exposed-jdbc")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<BootBuildImage> {
    imageName.set("alx-mag/${rootProject.name}-${project.name}")
}