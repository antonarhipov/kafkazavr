package io.kafkazavr.extension

import io.kafkazavr.feature.Kafka
import io.kafkazavr.kafka.buildProducer
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.webjars.*
import java.time.Duration

operator fun ApplicationConfig.get(key: String): String =
    propertyOrNull(key)?.getString() ?: ""

fun Application.module() {
    //region Install features
    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    install(Webjars)
    //endregion

    //region Configure routing
    routing {
        static("assets") {
            resources("META-INF/resources/assets")
        }
    }
    //endregion
}