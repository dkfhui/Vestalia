plugins {
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.jvm)
    kotlin("plugin.serialization") version "2.2.0"

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
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation(libs.ktor.server.websockets)
    testImplementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
