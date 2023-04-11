package io.kafkazavr

import io.kafkazavr.plugin.configureKafka
import io.kafkazavr.plugin.configureRouting
import io.kafkazavr.plugin.configureWebJars
import io.kafkazavr.plugin.configureWebSockets
import io.ktor.server.application.*

fun main(args: Array<String>) =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureWebJars()
    configureWebSockets()
    configureKafka()
    configureRouting()
}