package io.kafkazavr.driver

import io.kafkazavr.extension.actor
import io.ktor.application.*
import org.apache.kafka.clients.consumer.KafkaConsumer

fun Application.module() {
    actor("driver")
}
