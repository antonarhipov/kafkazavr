package io.kafkazavr.kafka

import io.ktor.application.*
import io.ktor.config.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerConfig.*
import java.util.*

fun <K, V> buildProducer(environment: ApplicationEnvironment): KafkaProducer<K, V> {
    val producerConfig = environment.config.config("ktor.kafka.producer")
    val commonConfig: ApplicationConfig = environment.config.config("ktor.kafka.properties") // take as map 
    
    val producerProps = Properties().apply {
        this[BOOTSTRAP_SERVERS_CONFIG] = producerConfig.property("bootstrap.servers").getList()
        this[CLIENT_ID_CONFIG] = producerConfig.property("client.id").getString()
        this[KEY_SERIALIZER_CLASS_CONFIG] = producerConfig.property("key.serializer").getString()
        this[VALUE_SERIALIZER_CLASS_CONFIG] = producerConfig.property("value.serializer").getString()
    }
    return KafkaProducer(producerProps)
}