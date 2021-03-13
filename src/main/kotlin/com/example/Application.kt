package com.example

import com.example.html.Html
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.webjars.*
import io.ktor.websocket.*
import io.micrometer.prometheus.*
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.title
import java.time.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    operator fun ApplicationConfig.get(key: String): String = property(key).getString()

    val kafka: ApplicationConfig = environment.config.config("ktor.kafka")
    val mapBox: ApplicationConfig = environment.config.config("ktor.mapbox")
    // println("Config: " + kafka.property("bootstrap-servers").getList())

    val consumer: ApplicationConfig = kafka.config("consumer")
    val producer: ApplicationConfig = kafka.config("producer")
    val properties: ApplicationConfig = kafka.config("properties")

    println("Consumer group id: ${consumer["group-id"]}")
    println("Protocol: ${properties["ssl.endpoint.identification.algorithm"]}")

    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
        // ...
    }
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(Webjars)
    routing {
        static("assets") {
            resources("META-INF/resources/assets")
        }
        get("/") {
            call.respondHtml(
                HttpStatusCode.OK,
                Html(mapBox["api-key"]).driverHTML
            )
        }
    }
    routing {
        get("/metrics-micrometer") {
            call.respond(appMicrometerRegistry.scrape())
        }
    }
    routing {
        webSocket("/") { // websocketSession
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        outgoing.send(Frame.Text("YOU SAID: $text"))
                        if (text.equals("bye", ignoreCase = true)) {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                        }
                    }
                    is Frame.Binary -> TODO()
                    is Frame.Close -> TODO()
                    is Frame.Ping -> TODO()
                    is Frame.Pong -> TODO()
                }
            }
        }
    }
}


