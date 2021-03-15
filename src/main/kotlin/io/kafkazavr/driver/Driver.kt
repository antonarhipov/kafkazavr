package io.kafkazavr.driver

import io.kafkazavr.extension.module
import io.kafkazavr.extension.get
import io.kafkazavr.html.Html
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*

fun Application.module() {

    val mapBox: ApplicationConfig = environment.config.config("ktor.mapbox")
    
    routing {
        get("/driver") {
            call.respondHtml(
                HttpStatusCode.OK,
                Html(mapBox["api-key"]).driverHTML
            )
        }

        webSocket("/driver") { // websocketSession
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