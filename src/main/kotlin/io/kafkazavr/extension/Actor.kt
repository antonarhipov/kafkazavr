package io.kafkazavr.extension

import com.typesafe.config.Config
import io.kafkazavr.html.Html
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
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.time.Duration

fun Application.actor(role: String) {
    val config: Config = parseConfiguration("src/main/resources/kafka-${role}.conf")

    val mapBox: ApplicationConfig = environment.config.config("ktor.mapbox")
    val producer: KafkaProducer<String, String> = buildProducer(config)
    
    val wsEndpointPath = "/${role}-ws"
    val wsUrl = "ws://" +
            //TODO: this doesn't seem to always work
//            InetAddress.getLocalHost().hostName + ":" +
            "localhost:" +
            environment.config.config("ktor.deployment")["port"] +
            wsEndpointPath

    lateinit var kafkaConsumer: KafkaConsumer<String, String>
    log.info("Websocket url: {}", wsUrl)
    routing {
        get("/${role}") {
            call.respondHtml(
                HttpStatusCode.OK,
                Html(mapBox["api-key"], wsUrl)[role]
            )
            log.info("Creating kafka consumer for $role")
            //TODO: ugly stuff
            kafkaConsumer = createKafkaConsumer(config, if (role == "driver") "rider" else "driver")
        }

        webSocket(wsEndpointPath) {
            try {
                for (frame in incoming) {
                    val text = (frame as Frame.Text).readText()
                    log.trace("Received frame: $text")
                    val json: JsonElement = Json.parseToJsonElement(text)
                    val key = json.jsonObject[role].toString()

                    producer.send(ProducerRecord(role, key, text))

                    kafkaConsumer.poll(Duration.ofMillis(100))
                        .map { it.value() as String }
                        .forEach { outgoing.send(Frame.Text(it)) }
                }
            } finally {
                kafkaConsumer.apply {
                    unsubscribe()
                    close()
                }
                log.info("consumer for $role unsubscribed and closed...")
            }
        }
    }
}