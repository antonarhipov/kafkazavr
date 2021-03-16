package io.kafkazavr.extension

import com.typesafe.config.ConfigFactory
import io.kafkazavr.feature.Kafka
import io.kafkazavr.kafka.buildProducer
import io.kafkazavr.kafka.configureKafkaTopics
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.webjars.*
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.p
import java.io.File
import java.time.Duration

operator fun ApplicationConfig.get(key: String): String =
    propertyOrNull(key)?.getString() ?: ""

fun Application.module() {

//    configureKafkaTopics(): TODO

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
        get("/") {
            call.respondHtml {
                body {
                    p {
                        a("/driver", "_blank") {
                            +"Driver"
                        }
                    }
                    p {
                        a("/rider", "_blank") {
                            +"rider"
                        }
                    }
                }
            }
        }
    }
    //endregion
}

fun String.splitPair(ch: Char): Pair<String, String>? = indexOf(ch).let { idx ->
    when (idx) {
        -1 -> null
        else -> Pair(take(idx), drop(idx + 1))
    }
}