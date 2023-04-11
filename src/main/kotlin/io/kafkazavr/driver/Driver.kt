package io.kafkazavr.driver

import io.kafkazavr.extension.actor
import io.ktor.server.application.*

@Suppress("unused")
fun Application.module() {
    actor("driver")
}
