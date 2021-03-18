package io.kafkazavr.kafka

import com.typesafe.config.Config
import io.kafkazavr.extension.configMap
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig
import java.util.*

fun <K, V> buildConsumer(config: Config): KafkaConsumer<K, V> {
    val bootstrapServers = config.getList("ktor.kafka.bootstrap.servers")

    // common config
    val commonConfig = configMap(config, "ktor.kafka.properties")

    // get consumer config
    val consumerConfig = configMap(config, "ktor.kafka.consumer")

    val consumerProperties: Properties = Properties().apply {
        putAll(commonConfig)
        putAll(consumerConfig)
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers.unwrapped())
    }
    return KafkaConsumer(consumerProperties)
}

fun <K, V> createKafkaConsumer(config: Config, topic: String): KafkaConsumer<K, V> {
    val consumer = buildConsumer<K, V>(config)
    consumer.subscribe(listOf(topic))
    return consumer
}
