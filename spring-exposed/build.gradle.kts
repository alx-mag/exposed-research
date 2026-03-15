plugins {
    id("buildsrc.convention.kotlin-jvm")
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("me.champeau.jmh") version "0.7.2"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "spring-exposed"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")

    implementation(platform("org.jetbrains.exposed:exposed-bom:1.1.1"))
    implementation("org.jetbrains.exposed:exposed-spring-boot4-starter")
    implementation("org.jetbrains.exposed:exposed-dao")
    implementation("org.jetbrains.exposed:exposed-jdbc")
    implementation("org.jetbrains.exposed:exposed-migration-jdbc")
    runtimeOnly("org.postgresql:postgresql")
//    runtimeOnly("com.h2database:h2")

    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    jmhImplementation("org.testcontainers:testcontainers-postgresql:2.0.3")
    jmhImplementation("org.testcontainers:testcontainers:2.0.3")
}

jmh {
    warmupIterations.set(3)
    iterations.set(5)
    fork.set(1)
    timeUnit.set("ms")
    benchmarkMode.addAll("avgt")
    resultFormat.set("JSON")
    resultsFile.set(project.layout.buildDirectory.file("results/jmh/results.json"))
    includes = listOf("DaoCrudBenchmark")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
