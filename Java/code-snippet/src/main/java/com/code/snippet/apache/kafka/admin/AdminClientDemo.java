package com.code.snippet.apache.kafka.admin;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.ConfigResource;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @description: Admin Client Demo.
 * @author: Ni Yanchun
 * @version: 1.0
 * @create: 2019-02-14 23:29
 **/
public class AdminClientDemo {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30000);

        try (AdminClient adminClient = AdminClient.create(props)) {
            describeCluster(adminClient);
            listAllTopics(adminClient);
            describeTopics(adminClient);
            describeConfig(adminClient);
        }
    }

    private static void describeCluster(AdminClient client) throws ExecutionException, InterruptedException {
        System.out.println("describeCluster:");

        DescribeClusterResult result = client.describeCluster();
        System.out.println(String.format("Cluster id: %s, controller: %s", result.clusterId().get(), result.controller().get()));

        System.out.println("Cluster node info:");
        for (Node node : result.nodes().get()) {
            System.out.println(node);
        }
        System.out.println();
    }

    private static void listAllTopics(AdminClient client) throws ExecutionException, InterruptedException {
        System.out.println("listAllTopics:");

        ListTopicsResult topicsResult = client.listTopics();
        Set<String> topics = topicsResult.names().get();
        System.out.println("Topics in cluster: " + topics);
        System.out.println();
    }

    private static void describeTopics(AdminClient client) throws ExecutionException, InterruptedException {
        System.out.println("describeTopics:");

        DescribeTopicsResult topicsResult = client.describeTopics(Arrays.asList("afa-platform", "afa-trade"));

        Map<String, TopicDescription> topics = topicsResult.all().get();
        for (Map.Entry<String, TopicDescription> entry : topics.entrySet()) {
            System.out.println(entry.getKey() + "==>" + entry.getValue());
        }
        System.out.println();
    }

    private static void describeConfig(AdminClient client) throws ExecutionException, InterruptedException {
        System.out.println("describeConfig:");

        DescribeConfigsResult configsResult = client.describeConfigs(Collections.singleton(
                new ConfigResource(ConfigResource.Type.TOPIC, "afa-trade")));
        Map<ConfigResource, Config> configs = configsResult.all().get();
        for (Map.Entry<ConfigResource, Config> entry : configs.entrySet()) {
            ConfigResource key = entry.getKey();
            Config value = entry.getValue();
            System.out.println(String.format("Resource type: %s, resource name: %s", key.type(), key.name()));
            Collection<ConfigEntry> configEntries = value.entries();
            for (ConfigEntry each : configEntries) {
                System.out.println(each.name() + " = " + each.value());
            }
        }
        System.out.println();
    }
}

