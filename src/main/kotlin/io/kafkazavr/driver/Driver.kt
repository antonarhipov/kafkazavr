package io.kafkazavr.driver

import io.kafkazavr.extension.get
import io.kafkazavr.html.Html
import io.kafkazavr.kafka.buildConsumer
import io.kafkazavr.kafka.buildProducer
import io.kafkazavr.kafka.createKafkaConsumer
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
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.net.InetAddress
import java.time.Duration

fun Application.module() {

    val mapBox: ApplicationConfig = environment.config.config("ktor.mapbox")
    val producer: KafkaProducer<String, String> = buildProducer(environment)
    val kafkaConsumer = createKafkaConsumer<String, String>(environment, "rider");
    val wsEndpointPath = "/driver-ws"
    val wsUrl = "ws://" +
            InetAddress.getLocalHost().hostName + ":" +
            environment.config.config("ktor.deployment")["port"] +
            wsEndpointPath

    log.info("Websocket url: {}", wsUrl)
    routing {
        get("/driver") {
            call.respondHtml(
                HttpStatusCode.OK,
                Html(mapBox["api-key"], wsUrl).driverHTML
            )
        }

        webSocket(wsEndpointPath) {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        val json: JsonElement = Json.parseToJsonElement(text)
                        val key = json.jsonObject["driver"].toString()

                        producer.send(ProducerRecord("driver", key, text))

                        kafkaConsumer.poll(Duration.ofMillis(100))
                            .map { it.value() as String }
                            .forEach { outgoing.send(Frame.Text(it)) }
                    }
                    is Frame.Binary -> TODO()
                    is Frame.Close -> {
                        // TODO
                        //kafkaConsumer.close()
                        close(CloseReason(CloseReason.Codes.NORMAL, "Bye!"))
                    }
                    is Frame.Ping -> TODO()
                    is Frame.Pong -> TODO()
                }
            }
        }
    }
}