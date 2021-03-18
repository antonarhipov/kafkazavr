package io.kafkazavr.extension

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

operator fun ApplicationConfig.get(key: String): String = propertyOrNull(key)?.getString() ?: ""

fun String.splitPair(ch: Char): Pair<String, String>? = indexOf(ch).let { idx ->
    when (idx) {
        -1 -> null
        else -> Pair(take(idx), drop(idx + 1))
    }
}

fun parseConfiguration(path: String): Config =
    ConfigFactory.parseFile(File(path))

fun configMap(config: Config, path: String): Map<String, Any> =
    config.getConfig(path).entrySet().associateBy({ it.key }, { it.value.unwrapped() })

inline fun <reified T> logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}