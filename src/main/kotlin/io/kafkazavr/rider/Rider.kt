package io.kafkazavr.rider

import io.kafkazavr.extension.actor
import io.ktor.application.*

fun Application.module() {
    actor("rider")
}