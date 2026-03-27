import buildsrc.Service
import buildsrc.convention.buildsrc.K6Test
import buildsrc.recreateComposeService
import buildsrc.runK6
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("buildsrc.convention.kotlin-jvm")
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "spring-exposed"

val prepareDb = rootProject.tasks.named<Exec>("prepareDb")

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

tasks.register<Exec>("deployContainer") {
    dependsOn("bootBuildImage")
    group = "deploy"
    description = "Build the spring-exposed image and recreate the spring-exposed container."
    recreateComposeService("spring-exposed")
}

tasks.register<Exec>(K6Test.GET_USERS) {
    dependsOn(prepareDb)
    group = "k6"
    runK6("get-test.js", Service.EXPOSED)
}

tasks.register<Exec>(K6Test.GET_USERS_FILTERING) {
    dependsOn(prepareDb)
    group = "k6"
    runK6("get-filtering-test.js", Service.EXPOSED)
}

tasks.register<Exec>("k6-GetRichUsers") {
    dependsOn(prepareDb)
    group = "k6"
    runK6("get-rich-test.js", Service.EXPOSED)
}

tasks.register<Exec>(K6Test.LOAD) {
    dependsOn(prepareDb)
    group = "k6"
    runK6("load-test.js", Service.EXPOSED)
}

tasks.withType<BootBuildImage> {
    imageName.set("alx-mag/${rootProject.name}-${project.name}")
}