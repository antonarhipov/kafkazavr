package io.kafkazavr.extension

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
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
import kotlinx.coroutines.channels.ClosedReceiveChannelException
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
import java.time.Duration

operator fun ApplicationConfig.get(key: String): String =
    propertyOrNull(key)?.getString() ?: ""

fun Application.module() {

//    configureKafkaTopics(): TODO

    //region Install features
    install(WebSockets) {
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
                            +"Rider"
                        }
                    }
                }
            }
        }
    }
    //endregion
}

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

fun String.splitPair(ch: Char): Pair<String, String>? = indexOf(ch).let { idx ->
    when (idx) {
        -1 -> null
        else -> Pair(take(idx), drop(idx + 1))
    }
}

fun parseConfiguration(path: String): Config {
//    val configFile = ArgsHolder.args.mapNotNull { it.splitPair('=') }.toMap()["-config"]?.let { File(it) }
    val configFile = File(path)
    val config: Config = ConfigFactory.parseFile(configFile)
    return config
}

fun configMap(config: Config, path: String): Map<String, Any> {
    val map = config.getConfig(path).entrySet().associateBy({ it.key }, { it.value.unwrapped() })
    return map
}