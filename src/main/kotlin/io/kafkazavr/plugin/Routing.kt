package io.kafkazavr.plugin

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.p

fun Application.configureRouting() {
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
}