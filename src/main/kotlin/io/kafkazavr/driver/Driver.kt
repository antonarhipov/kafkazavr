package io.kafkazavr.driver

import io.kafkazavr.extension.actor
import io.ktor.server.application.*

fun Application.module() {
    actor("driver")
}
