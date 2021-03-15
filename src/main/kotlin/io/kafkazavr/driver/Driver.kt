package io.kafkazavr.driver

import io.kafkazavr.extension.get
import io.kafkazavr.html.Html
import io.kafkazavr.kafka.buildProducer
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

fun Application.module() {

    val mapBox: ApplicationConfig = environment.config.config("ktor.mapbox")

    val producer: KafkaProducer<String, String> = buildProducer(environment)

    routing {
        get("/driver") {
            call.respondHtml(
                HttpStatusCode.OK,
                Html(mapBox["api-key"], "ws://localhost:8080/driver-ws").driverHTML
            )
        }

        webSocket("/driver-ws") {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        val json: JsonElement = Json.parseToJsonElement(text)
                        val key = json.jsonObject["driver"].toString()
                        producer.send(ProducerRecord("driver", key, text))
                    }
                    is Frame.Binary -> TODO()
                    is Frame.Close -> close(CloseReason(CloseReason.Codes.NORMAL, "Bye!"))
                    is Frame.Ping -> TODO()
                    is Frame.Pong -> TODO()
                }
            }
        }
    }
}