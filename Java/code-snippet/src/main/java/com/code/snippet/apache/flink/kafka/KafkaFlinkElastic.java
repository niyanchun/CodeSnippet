package com.code.snippet.apache.flink.kafka;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.util.*;

/**
 * @description: A demo flink streaming program that read from kafka and then write to elasticsearch.
 * @author: NiYanchun
 * @version: 1.0
 * @create: 2019-03-11 23:06
 **/
public class KafkaFlinkElastic {

    public static void main(String[] args) throws Exception {

        final ParameterTool params = ParameterTool.fromArgs(args);
        final String kafkaBrokers = params.get("brokers", "127.0.0.1:9092");

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // step 1: read data from kafka
        DataStream<String> dataStream = readFromKafka(env, kafkaBrokers);

        // step 2: write data to es
        writeToElastic(dataStream);

        env.execute("KafkaFlinkElastic");
    }

    private static DataStream<String> readFromKafka(StreamExecutionEnvironment env, String brokers) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", brokers);
        properties.setProperty("group.id", "test-group");

        return env.addSource(
                new FlinkKafkaConsumer010<>("test", new SimpleStringSchema(), properties)
                        .setStartFromEarliest());
    }

    private static void writeToElastic(DataStream<String> input) {
        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost("127.0.0.1", 9200, "http"));

        ElasticsearchSink.Builder<String> esSinkBuilder = new ElasticsearchSink.Builder<>(httpHosts,
                new ElasticsearchSinkFunction<String>() {

                    private IndexRequest createIndexRequest(String element) {
                        Map<String, Object> json = new HashMap<>();
                        json.put("message", element);

                        return Requests.indexRequest()
                                .index("test-index")
                                .type("doc")
                                .source(json);
                    }


                    @Override
                    public void process(String element, RuntimeContext ctx, RequestIndexer indexer) {
                        indexer.add(createIndexRequest(element));
                    }
                });

        // custom es sink
        esSinkBuilder.setRestClientFactory(restClientBuilder -> {
            restClientBuilder.setMaxRetryTimeoutMillis(30000);
        });

        esSinkBuilder.setBulkFlushMaxActions(1);

        // register failure handler
        esSinkBuilder.setFailureHandler((action, failure, restStatusCode, indexer) ->
                System.out.println(failure.getMessage())
        );

        // finally, build and add the sink to the job's pipeline
        input.addSink(esSinkBuilder.build());
    }
}