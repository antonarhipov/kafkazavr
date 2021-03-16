package io.kafkazavr.kafka

import io.kafkazavr.extension.get
import io.ktor.application.*
import io.ktor.config.*
import org.apache.kafka.clients.consumer.ConsumerConfig.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.*


fun <K, V> buildConsumer(environment: ApplicationEnvironment): KafkaConsumer<K, V> {
    val config: ApplicationConfig = environment.config.config("ktor.kafka.consumer")
    val kafka = environment.config.config("ktor.kafka")
    //val commonConfig: ApplicationConfig = environment.config.config("ktor.kafka.properties")

    val producerProps = Properties().apply {
        this[BOOTSTRAP_SERVERS_CONFIG] = kafka.property("bootstrap.servers").getList()
        this[GROUP_ID_CONFIG] = config.property("group.id").getString()
        this[KEY_DESERIALIZER_CLASS_CONFIG] = config["key.deserializer"]
        this[VALUE_DESERIALIZER_CLASS_CONFIG] = config["value.deserializer"]

        /*this["ssl.endpoint.identification.algorithm"] = commonConfig["ssl.endpoint.identification.algorithm"]
        this["sasl.mechanism"] = commonConfig["sasl.mechanism"]
        this["request.timeout.ms"] = commonConfig["request.timeout.ms"]
        this["retry.backoff.ms"] = commonConfig["retry.backoff.ms"]
        this["sasl.jaas.config"] = commonConfig["sasl.jaas.config"]
        this["security.protocol"] = commonConfig["security.protocol"]*/
    }

    return KafkaConsumer(producerProps)

}

fun <K, V> createKafkaConsumer(
    environment: ApplicationEnvironment,
    topic: String
): KafkaConsumer<K, V> {
    val consumer = buildConsumer<K, V>(environment)
    consumer.subscribe(listOf(topic))
    return consumer
}