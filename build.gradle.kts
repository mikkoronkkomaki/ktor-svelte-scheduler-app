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

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)

    sourceSets {
        main {
            kotlin.srcDirs("backend/src/main/kotlin")
        }
        test {
            kotlin.srcDirs("backend/src/test/kotlin")
        }
    }
}

sourceSets {
    named("main") {
        resources.srcDir("backend/src/main/resources")
    }
    named("test") {
        resources.srcDir("backend/src/test/resources")
    }
}

dependencies {
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.cors)
    implementation(ktorLibs.server.netty)
    implementation(libs.logback.classic)
    implementation("org.postgresql:postgresql:42.7.10")
    implementation("com.zaxxer:HikariCP:5.1.0")

    
    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
    testImplementation("io.mockk:mockk:1.14.9")
    testImplementation("io.ktor:ktor-client-mock:3.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.14.3")
    testImplementation("org.testcontainers:junit-jupiter:1.20.1")
    testImplementation("org.testcontainers:postgresql:1.20.1")
    testImplementation("org.flywaydb:flyway-core:13.3.0")
    testImplementation("org.flywaydb:flyway-database-postgresql:13.3.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.14.3")
}
