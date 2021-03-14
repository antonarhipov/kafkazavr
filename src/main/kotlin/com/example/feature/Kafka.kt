package com.example.feature

import io.ktor.application.*
import io.ktor.config.*
import io.ktor.util.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val log: Logger = LoggerFactory.getLogger("com.example.feature.Kafka")

class Kafka(configuration: Configuration) {

    private val someParameterName = configuration.someParameterName

    class Configuration {
        var someParameterName : String? = null

    }

    companion object Feature : ApplicationFeature<Application, Configuration, Kafka> {
        override val key: AttributeKey<Kafka>
            get() = AttributeKey("Kafka")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): Kafka {
            val configuration = Configuration().apply(configure)
            val kafkaFeature = Kafka(configuration)

            log.info(">>>>>>> Configuring Kafka feature")

            operator fun ApplicationConfig.get(key: String): String = property(key).getString()

            val kafka: ApplicationConfig = pipeline.environment.config.config("ktor.kafka")
            val mapBox: ApplicationConfig = pipeline.environment.config.config("ktor.mapbox")
            // println("Config: " + kafka.property("bootstrap-servers").getList())

            val consumer: ApplicationConfig = kafka.config("consumer")
            val producer: ApplicationConfig = kafka.config("producer")
            val properties: ApplicationConfig = kafka.config("properties")

            log.info(">>>>>>> Consumer group id: ${consumer["group-id"]}")
            log.info(">>>>>>> Protocol: ${properties["ssl.endpoint.identification.algorithm"]}")

            return kafkaFeature
        }

    }
}