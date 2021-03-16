package io.kafkazavr.kafka

import com.typesafe.config.Config
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.*
import java.util.*

fun <K, V> buildProducer(config: Config): KafkaProducer<K, V> {
    val bootstrapServers = config.getList("ktor.kafka.bootstrap.servers")

    // common config
    val commonConfig = config.getConfig("ktor.kafka.properties").entrySet().associateBy({ it.key }, { it.value.unwrapped() })

    // get producer config
    val producerConfig = config.getConfig("ktor.kafka.producer").entrySet().associateBy({ it.key }, { it.value.unwrapped() })
    // creating properties
    val producerProperties: Properties = Properties().apply {
        putAll(producerConfig)
        putAll(commonConfig)
        put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers.unwrapped())
    }

    return KafkaProducer(producerProperties)
}
