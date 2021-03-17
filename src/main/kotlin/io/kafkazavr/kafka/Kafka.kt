package io.kafkazavr.kafka

import io.ktor.application.*
import io.ktor.util.*
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.common.config.TopicConfig
import java.util.*

class Kafka(configuration: Configuration) {
    private val configurationPath = configuration.configurationPath
    private val topics = configuration.topics

    class Configuration {
        var configurationPath: String = ""
        var topics: List<String> = emptyList()
    }

    private fun createTopics() {
        val properties: Properties = getProperties(configurationPath)

        val adminClient = AdminClient.create(properties)
        val createTopicsResult = adminClient.createTopics(
            topics.map {
                newTopic(it) {
                    partitions = 3
                    replicas = 1
                    configs = mapOf(
                        TopicConfig.CLEANUP_POLICY_COMPACT to "compact",
                    )
                }
            }
        )

        //TODO: check createTopicsResult. If anything's wrong - throw exception?
    }

    private fun getProperties(configurationPath: String?): Properties {
        val properties = Properties()
        properties.setProperty("a", "b")
        return properties
    }

    companion object Feature : ApplicationFeature<Application, Configuration, Kafka> {
        override val key: AttributeKey<Kafka>
            get() = AttributeKey("Kafka")

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): Kafka {
            val configuration = Configuration().apply(configure)
            val kafkaFeature = Kafka(configuration)

            kafkaFeature.createTopics()

            return kafkaFeature
        }

    }
}