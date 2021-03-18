package io.kafkazavr

import io.kafkazavr.kafka.Kafka
import io.kafkazavr.kafka.newTopic
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.webjars.*
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.p
import java.time.Duration

fun main(args: Array<String>) =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    //region Webjars
    install(Webjars)
    //endregion

    //region Websockets
    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    //endregion

    //region Kafka
    install(Kafka) {
        configurationPath = "src/main/resources/kafka-driver.conf"
        topics = listOf(
            newTopic("rider") {
                partitions = 3
                replicas = 1 // for docker
                //replicas = 3 // for cloud
            },
            newTopic("driver") {
                partitions = 3
                replicas = 1 // for docker
                //replicas = 3 // for cloud
            }
        )
    }
    //endregion

    //region Routing
    routing {
        //region static assets location
        static("assets") {
            resources("META-INF/resources/assets")
        }
        //endregion
        get("/") {
            call.respondHtml {
                body {
                    p {
                        a("/driver", "_blank") { +"Driver" }
                    }
                    p {
                        a("/rider", "_blank") { +"Rider" }
                    }
                }
            }
        }
    }
    //endregion
}
