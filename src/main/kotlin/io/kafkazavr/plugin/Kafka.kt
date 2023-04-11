package io.kafkazavr.plugin

import io.confluent.developer.ktor.Kafka
import io.confluent.developer.ktor.newTopic
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureKafka() {
    install(Kafka) {
        configurationPath = "src/main/resources/kafka-driver.conf"
        topics = listOf(
            newTopic("rider") {
                partitions = 3
                //replicas = 1 // for docker
                replicas = 3 // for cloud
            },
            newTopic("driver") {
                partitions = 3
                //replicas = 1 // for docker
                replicas = 3 // for cloud
            }
        )
    }
}