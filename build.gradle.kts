plugins {
    alias(libs.plugins.ktor)
    kotlin("jvm") version "2.2.0"
}

group = "com.dkfhui"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
//    Default seems to be Java 23, so I have to explicitly specify to use Java 24
    jvmToolchain(24)
}

dependencies {
    implementation("io.ktor:ktor-server-status-pages")
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
