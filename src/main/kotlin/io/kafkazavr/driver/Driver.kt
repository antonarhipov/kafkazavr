package io.kafkazavr.driver

import com.fasterxml.jackson.databind.ObjectMapper
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
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

fun Application.module() {

    val mapBox: ApplicationConfig = environment.config.config("ktor.mapbox")

    val producer: KafkaProducer<String, String> = buildProducer(environment)

    routing {
        get("/driver") {
            call.respondHtml(
                HttpStatusCode.OK,
                Html(mapBox["api-key"]).driverHTML
            )
        }

        webSocket("/driver") {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        val mapper = ObjectMapper() // TODO: reuse
                        val message = mapper.readTree(text) // TODO: avoid blocking call
                        val key: String = message.get("driver").asText()
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