package com.niyanchun;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Elasticsearch;
import org.apache.flink.table.descriptors.Json;
import org.apache.flink.table.descriptors.Kafka;
import org.apache.flink.table.descriptors.Schema;


/**
 * Load data from Kafka and write to ES with Flink Table API.
 *
 * @author NiYanchun
 **/
public class Kafka2EsByTableApi {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);

        // 注册Kafka为Source
        tableEnv.connect(
                new Kafka()
                        .version("universal")
                        .topic("kafka2es-test")
                        .startFromEarliest()
                        .property("bootstrap.servers", "10.8.4.42:9092")
                        .property("group.id", "test")
        ).withFormat(
                new Json()
                        .failOnMissingField(false)
                        .deriveSchema()
        ).withSchema(
                new Schema()
                        .field("name", Types.STRING)
        ).inAppendMode().registerTableSource("kafkaTableSource");

        // 使用Table API读取数据
        Table kafkaSource = tableEnv.scan("kafkaTableSource");

        // 注册ES为sink
        tableEnv.connect(
                new Elasticsearch()
                        .version("6")
                        .host("10.8.4.42", 9200, "http")
                        .index("kafka2es-test")
                        .documentType("doc")
                        // 为了方便测试，将bulkFlushMaxActions设置为 1
                        .bulkFlushMaxActions(1)
        ).withFormat(
                new Json()
                        .failOnMissingField(false)
                        .deriveSchema()
        ).withSchema(
                new Schema()
                        .field("name", Types.STRING)
        ).inAppendMode().registerTableSink("esSink");

        kafkaSource.insertInto("esSink");

        tableEnv.execute("Kafka2EsByTableAPI");
    }
}
