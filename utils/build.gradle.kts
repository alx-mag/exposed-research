plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    // Apply the kotlinx bundle of dependencies from the version catalog (`gradle/libs.versions.toml`).
    implementation(libs.bundles.kotlinxEcosystem)
    testImplementation(kotlin("test"))

    compileOnly("org.openjdk.jmh:jmh-core:1.37")
    compileOnly("org.springframework.boot:spring-boot")
    compileOnly("org.testcontainers:testcontainers:2.0.3")
    compileOnly("org.testcontainers:testcontainers-postgresql:2.0.3")
}