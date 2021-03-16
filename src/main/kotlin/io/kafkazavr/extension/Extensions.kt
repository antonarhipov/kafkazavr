package io.kafkazavr.extension

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.kafkazavr.ArgsHolder
import io.kafkazavr.html.Html
import io.kafkazavr.kafka.buildProducer
import io.kafkazavr.kafka.createKafkaConsumer
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.webjars.*
import io.ktor.websocket.*
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.p
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.io.File
import java.net.InetAddress
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

lateinit var kafkaConsumer: KafkaConsumer<String, String>

fun Application.actor(role: String) {
    val config: Config = parseConfiguration()

    val mapBox: ApplicationConfig = environment.config.config("ktor.mapbox")
    val producer: KafkaProducer<String, String> = buildProducer(config)

    val wsEndpointPath = "/${role}-ws"
    val wsUrl = "ws://" +
            InetAddress.getLocalHost().hostName + ":" +
            environment.config.config("ktor.deployment")["port"] +
            wsEndpointPath

    log.info("Websocket url: {}", wsUrl)
    routing {
        get("/${role}") {
            kafkaConsumer = createKafkaConsumer(config, "rider")

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
                        val key = json.jsonObject[role].toString()

                        producer.send(ProducerRecord(role, key, text))

                        kafkaConsumer.poll(Duration.ofMillis(100))
                            .map { it.value() as String }
                            .forEach { outgoing.send(Frame.Text(it)) }
                    }
                    is Frame.Binary -> TODO()
                    is Frame.Close -> {
                        kafkaConsumer.close()
                        close(CloseReason(CloseReason.Codes.NORMAL, "Bye!"))
                    }
                    is Frame.Ping -> TODO()
                    is Frame.Pong -> TODO()
                }
            }
        }
    }
}

fun String.splitPair(ch: Char): Pair<String, String>? = indexOf(ch).let { idx ->
    when (idx) {
        -1 -> null
        else -> Pair(take(idx), drop(idx + 1))
    }
}

fun parseConfiguration(): Config {
//    val configFile = ArgsHolder.args.mapNotNull { it.splitPair('=') }.toMap()["-config"]?.let { File(it) }
    val configFile = File("src/main/resources/kafka.conf")
    val config: Config = ConfigFactory.parseFile(configFile)
    return config
}