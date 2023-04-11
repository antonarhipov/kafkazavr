package io.kafkazavr.rider

import io.kafkazavr.extension.actor
import io.ktor.server.application.*

@Suppress("unused")
fun Application.module() {
    actor("rider")
}