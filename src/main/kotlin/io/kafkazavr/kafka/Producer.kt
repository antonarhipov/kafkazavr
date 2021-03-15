package io.kafkazavr.kafka

import io.kafkazavr.extension.get
import io.ktor.application.*
import io.ktor.config.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.*
import java.util.*

fun <K, V> buildProducer(environment: ApplicationEnvironment): KafkaProducer<K, V> {
    val config: ApplicationConfig = environment.config.config("ktor.kafka.producer")
    val kafka = environment.config.config("ktor.kafka")
    val commonConfig: ApplicationConfig = environment.config.config("ktor.kafka.properties")

    val producerProps = Properties().apply {
        this[BOOTSTRAP_SERVERS_CONFIG] = kafka.property("bootstrap.servers").getList()
        this[CLIENT_ID_CONFIG] = config.property("client.id").getString()
        this[KEY_SERIALIZER_CLASS_CONFIG] = config["key.serializer"]
        this[VALUE_SERIALIZER_CLASS_CONFIG] = config["value.serializer"]

        /*this["ssl.endpoint.identification.algorithm"] = commonConfig["ssl.endpoint.identification.algorithm"]
        this["sasl.mechanism"] = commonConfig["sasl.mechanism"]
        this["request.timeout.ms"] = commonConfig["request.timeout.ms"]
        this["retry.backoff.ms"] = commonConfig["retry.backoff.ms"]
        this["sasl.jaas.config"] = commonConfig["sasl.jaas.config"]
        this["security.protocol"] = commonConfig["security.protocol"]*/
    }
    return KafkaProducer(producerProps)
}
