plugins {
    id("buildsrc.convention.spring")
}

description = "spring-exposed"

dependencies {
    implementation(platform("org.jetbrains.exposed:exposed-bom:1.1.1"))
    implementation("org.jetbrains.exposed:exposed-spring-boot4-starter")
    implementation("org.jetbrains.exposed:exposed-dao")
    implementation("org.jetbrains.exposed:exposed-jdbc")
}
