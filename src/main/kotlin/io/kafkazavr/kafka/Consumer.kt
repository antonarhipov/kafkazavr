package io.kafkazavr.kafka

import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig
import java.util.*


fun <K, V> buildConsumer(config: Config): KafkaConsumer<K, V> {
    val bootstrapServers = config.getList("ktor.kafka.bootstrap.servers")

    // common config
    val commonConfig =
        config.getConfig("ktor.kafka.properties").entrySet().associateBy({ it.key }, { it.value.unwrapped() })

    // get consumer config
    val consumerConfig =
        config.getConfig("ktor.kafka.consumer").entrySet().associateBy({ it.key }, { it.value.unwrapped() })

    val consumerProperties: Properties = Properties().apply {
        putAll(commonConfig)
        putAll(consumerConfig)
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers.unwrapped())
    }

    return KafkaConsumer(consumerProperties)
}

fun <K, V> createKafkaConsumer(
    config: Config,
    topic: String,
): KafkaConsumer<K, V> {
    val consumer = buildConsumer<K, V>(config)
    consumer.subscribe(listOf(topic))
    return consumer
}