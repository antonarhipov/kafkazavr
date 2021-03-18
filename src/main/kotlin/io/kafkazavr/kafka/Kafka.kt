package io.kafkazavr.kafka

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.kafkazavr.extension.configMap
import io.kafkazavr.extension.logger
import io.ktor.application.*
import io.ktor.util.*
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.CreateTopicsResult
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import java.io.File
import java.util.*

class Kafka(configuration: Configuration) {
    private val log = logger<Kafka>()
    private val configurationPath = configuration.configurationPath

    private val topics = configuration.topics

    class Configuration {
        var configurationPath: String = ""
        var topics: List<NewTopic> = emptyList()
    }

    private fun createTopics() {
        val properties: Properties = getProperties(configurationPath)

        val adminClient = AdminClient.create(properties)
        val createTopicsResult: CreateTopicsResult = adminClient.createTopics(topics)

        createTopicsResult.values().forEach { (k, _) ->
            log.debug("Topic {} created...", k)
        }
    }

    private fun getProperties(configurationPath: String): Properties {
        val configFile = File(configurationPath)
        val config: Config = ConfigFactory.parseFile(configFile)

        val bootstrapServers = config.getList("ktor.kafka.bootstrap.servers")
        val commonConfig = configMap(config, "ktor.kafka.properties")

        return Properties().apply {
            putAll(commonConfig)
            put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers.unwrapped())
        }
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