package io.kafkazavr.rider

import io.kafkazavr.extension.actor
import io.ktor.server.application.*

fun Application.module() {
    actor("rider")
}