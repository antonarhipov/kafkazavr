package io.kafkazavr.plugin

import io.ktor.server.application.*
import io.ktor.server.webjars.*

fun Application.configureWebJars() {
    install(Webjars)
}