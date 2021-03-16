package io.kafkazavr.kafka

import org.apache.kafka.clients.admin.NewTopic

class TopicBuilder constructor(private val name: String) {
    var partitions = 1
    var replicas: Short = 1
    var replicasAssignments: MutableMap<Int, List<Int?>>? = null
    var configs: Map<String, String>? = null

//    fun compact(): TopicBuilder {
//        configs[TopicConfig.CLEANUP_POLICY_CONFIG] = TopicConfig.CLEANUP_POLICY_COMPACT
//        return this
//    }

    fun build(): NewTopic {
        val topic = if (replicasAssignments == null)
            NewTopic(name, partitions, replicas)
        else
            NewTopic(name, replicasAssignments)

        configs?.let {
            if (it.isNotEmpty()) {
                topic.configs(configs)
            }
        }

        return topic
    }

}