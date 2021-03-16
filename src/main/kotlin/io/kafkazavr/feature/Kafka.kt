package io.kafkazavr.feature

import io.kafkazavr.kafka.buildProducer
import io.ktor.application.*
import io.ktor.util.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val log: Logger = LoggerFactory.getLogger("com.example.feature.Kafka")

class Kafka(configuration: Configuration) {

    private val someParameterName = configuration.someParameterName

    lateinit var producer: KafkaProducer<String, String>

    class Configuration {
        var someParameterName: String? = null
    }

    companion object Feature : ApplicationFeature<Application, Configuration, Kafka> {
        override val key: AttributeKey<Kafka>
            get() = AttributeKey("Kafka")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): Kafka {
            val configuration = Configuration().apply(configure)
            val kafkaFeature = Kafka(configuration)
//            kafkaFeature.producer = buildProducer(pipeline.environment)
            return kafkaFeature
        }

    }
}