package com.exithere.rain.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class RestTemplateConfig {
    private static final int  HTTP_MAX_IDLE_CONNECTION = 50; // 환경설정에서 읽어오도록 변경 필요.
    private static final long HTTP_KEEP_ALIVE_DURATION = 20;
    private static final long HTTP_CONNECTION_TIMEOUT = 30;
    private static final long HTTP_READ_TIMEOUT = 30;

    @Bean
    public RestTemplate restTemplate() {
        ConnectionPool pool = new ConnectionPool(HTTP_MAX_IDLE_CONNECTION, HTTP_KEEP_ALIVE_DURATION, TimeUnit.SECONDS);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(pool)
                .connectTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(client);

        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(factory));
        restTemplate.setInterceptors(Collections.singletonList(new RestTemplateLoggingInterceptor()));
        return restTemplate;
    }
}

@Slf4j
class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        // request log
        this.traceRequest(request, body);

        ClientHttpResponse response = execution.execute(request, body);

        // response log
        this.traceResponse(response);

        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        log.debug("===== RestTemplate Request Begin ====================");
        log.debug("URI : {}", request.getURI());
        log.debug("Method : {}", request.getMethod());
        log.debug("Headers : {}", request.getHeaders());
        log.debug("Body : {}", new String(body, StandardCharsets.UTF_8));
        log.debug("===== RestTemplate Request End   ====================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        if (response == null) {
            return;
        }
        InputStreamReader isr = new InputStreamReader(response.getBody(), StandardCharsets.UTF_8);
        String body = new BufferedReader(isr).lines().collect(Collectors.joining("\n"));

        log.debug("===== RestTemplate Response Begin ===================");
        log.debug("Status code : {}", response.getStatusCode());
        log.debug("Status text : {}", response.getStatusText());
        //log.debug("Headers : {}", response.getHeaders());
        if (response.getHeaders().containsKey(HttpHeaders.CONTENT_DISPOSITION)) {
            //log.debug("Body : Files InputStream...");
        } else {
            //log.debug("Body : {}", body);
        }
        log.debug("===== RestTemplate Response End   ===================");
    }

}
