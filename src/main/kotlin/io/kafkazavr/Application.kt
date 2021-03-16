package io.kafkazavr

import io.kafkazavr.extension.splitPair
import java.io.File

fun main(args: Array<String>) {
    // TODO workaround!!! 
    ArgsHolder.args = args
    io.ktor.server.netty.EngineMain.main(args)
}

object ArgsHolder {
    lateinit var args: Array<String>
}