package com.niyanchun;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Load data from kafka and write to ES with Stream API.
 *
 * @author NiYanchun
 **/
public class Kafka2EsByStreamApi {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "10.8.4.42:9092");
        properties.setProperty("group.id", "test");

        FlinkKafkaConsumer<String> flinkKafkaConsumer =
                new FlinkKafkaConsumer<>("kafka2es-test", new SimpleStringSchema(), properties);
        flinkKafkaConsumer.setStartFromEarliest();

        DataStream<String> stream =
                env.addSource(flinkKafkaConsumer);


        stream.addSink(getEsSink());

        env.execute("");
    }]

    private static ElasticsearchSink<String> getEsSink() {
        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost("10.8.4.42", 9200, "http"));

        ElasticsearchSink.Builder<String> esSinkBuilder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<String>() {
                    private IndexRequest createIndexRequest(String element) {
                        return Requests.indexRequest()
                                .index("kafka2es-test")
                                .type("doc")
                                .source(element, XContentType.JSON);
                    }

                    @Override
                    public void process(String s, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
                        requestIndexer.add(createIndexRequest(s));
                    }
                }
        );

        esSinkBuilder.setBulkFlushMaxActions(1);

        return esSinkBuilder.build();
    }
}
