val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val ak_version: String by project

plugins {
    application
    kotlin("jvm") version "1.8.20"
    id("io.ktor.plugin") version "2.2.4"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.avast.gradle.docker-compose") version "0.16.12"
}

group = "org.kafkainaction"
version = "0.0.1"

application {
    mainClass.set("io.kafkazavr.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dockerCompose.isRequiredBy(project.tasks.named("run"))

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClass
            )
        )
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.BELLSOFT)
    }
//    targetCompatibility = JavaVersion.VERSION_17
//    sourceCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-webjars")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-jackson")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-html-builder")
    implementation("io.ktor:ktor-server-websockets")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    //region Kafka and Confluent
    implementation("org.apache.kafka:kafka-clients:$ak_version")
    implementation("com.github.gamussa:ktor-kafka:89ebc28cf9")
    //endregion

    //region webjars
    implementation("org.webjars:vue:2.1.3")
    implementation("org.webjars:ionicons:2.0.1")
    implementation("org.webjars.npm:google-polyline:1.0.0")
    //endregion

    testImplementation("io.ktor:ktor-server-tests")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}