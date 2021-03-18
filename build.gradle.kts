val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val prometeus_version: String by project

plugins {
    application
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.avast.gradle.docker-compose") version "0.14.1"
}

group = "com.example"
version = "0.0.1"

application {
    // TODO: mainClass.set doesn't work with shadowJar ???
    // ref https://ktor.io/docs/fatjar.html#fat-jar-gradle 
    mainClassName = "io.kafkazavr.ApplicationKt"
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-metrics-micrometer:$ktor_version")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometeus_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
    //region Kafka and Confluent
    implementation("org.apache.kafka:kafka-clients:2.7.0")

    //endregion

    //region webjars
    implementation("io.ktor:ktor-webjars:$ktor_version")
    implementation("org.webjars:vue:2.1.3")
    implementation("org.webjars:ionicons:2.0.1")
    implementation("org.webjars.npm:google-polyline:1.0.0")
    //endregion

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}