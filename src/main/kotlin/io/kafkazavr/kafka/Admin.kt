package io.kafkazavr.kafka

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.CreateTopicsResult
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.config.TopicConfig
import java.util.*
/*
    example of Kafka Admin API usage
 */
@SuppressWarnings("unused")
fun configureKafkaTopics(config: Properties, vararg topics: String): CreateTopicsResult {
    return kafkaAdmin(config) {
        createTopics(topics.map {
            newTopic(it) {
                partitions = 3
                replicas = 1
                configs = mapOf(
                    TopicConfig.CLEANUP_POLICY_COMPACT to "compact",
                )
            }
        })
    }
}

fun kafkaAdmin(props: Properties, block: AdminClient.() -> CreateTopicsResult): CreateTopicsResult =
    AdminClient.create(props).run(block)

fun newTopic(name: String, block: TopicBuilder.() -> Unit): NewTopic =
    TopicBuilder(name).apply(block).build()


