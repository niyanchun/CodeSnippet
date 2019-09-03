package com.niyanchun.elastic;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: ElasticSearch Client Factory.
 * @author: NiYanchun
 * @version: 1.0
 * @create: 2019-03-11 0:15
 **/
public class RestClientFactory {
    private static final String HOSTNAME = "127.0.0.1";
    private static final int DEFAULT_PORT = 9200;
    private static final String DEFAULT_SCHEMA = "http";

    private static RestClient restLowLevelClient;
    private static RestHighLevelClient restHighLevelClient;

    private static void init() {
        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost(HOSTNAME, DEFAULT_PORT, DEFAULT_SCHEMA));


        RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[0]));

        // custom client as you wish
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(60000);
            requestConfigBuilder.setConnectionRequestTimeout(60000);
            requestConfigBuilder.setSocketTimeout(60000);

            return requestConfigBuilder;
        });
        builder.setMaxRetryTimeoutMillis(60000);

        restLowLevelClient = builder.build();
        restHighLevelClient = new RestHighLevelClient(builder);
    }

    public static RestClient getRestLowLevelClient() {
        if (restLowLevelClient == null) {
            init();
        }

        return restLowLevelClient;
    }

    public static RestHighLevelClient getRestHighLevelClient() {
        if (restHighLevelClient == null) {
            init();
        }

        return restHighLevelClient;
    }

    public static void close() {
        if (restHighLevelClient != null) {
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                e.getMessage();
            }
        }
    }
}
