package io.kafkazavr

fun main(args: Array<String>) {
    // TODO workaround!!!
    // This workaround doesn't work. args are not visible in a module.
    ArgsHolder.args = args
    io.ktor.server.netty.EngineMain.main(args)
}

object ArgsHolder {
    lateinit var args: Array<String>
}