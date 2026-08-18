plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

application {
    mainClass.set("com.example.ApplicationKt")
}

tasks.named<JavaExec>("run") {
    systemProperty("io.ktor.development", "true")
}

kotlin {
    jvmToolchain(21)
}
dependencies {
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(libs.logback.classic)
    implementation("org.postgresql:postgresql:42.7.10")
    implementation("com.zaxxer:HikariCP:5.1.0")
    
    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}
