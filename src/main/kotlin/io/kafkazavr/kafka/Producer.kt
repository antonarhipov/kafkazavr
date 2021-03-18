package io.kafkazavr.kafka

import com.typesafe.config.Config
import io.kafkazavr.extension.configMap
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.*
import java.util.*

fun <K, V> buildProducer(config: Config): KafkaProducer<K, V> {
    val bootstrapServers = config.getList("ktor.kafka.bootstrap.servers")
    // common config
    val commonConfig = configMap(config, "ktor.kafka.properties")
    // get producer config
    val producerConfig = configMap(config, "ktor.kafka.producer")
    // creating properties
    val producerProperties: Properties = Properties().apply {
        putAll(producerConfig)
        putAll(commonConfig)
        put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers.unwrapped())
    }
    return KafkaProducer(producerProperties)
}
