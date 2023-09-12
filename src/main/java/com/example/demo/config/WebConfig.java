package com.example.demo.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.WebFilter;
import reactor.netty.http.client.HttpClient;
import reactor.util.context.Context;

import java.time.Duration;

import static com.example.demo.constant.LoggerConst.PATH_URI;
import static com.example.demo.constant.LoggerConst.TRX_ID;

@Configuration
public class WebConfig {

    @Bean
    public WebClient petStoreClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(10))
                        .addHandlerLast(new WriteTimeoutHandler(10)))
                .responseTimeout((Duration.ofSeconds(2)));

        return WebClient.builder()
                .baseUrl("https://petstore3.swagger.io/api/v3")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebFilter slf4jMdcFilter() {
        return (exchange, chain) -> {
            String requestId = exchange.getRequest().getId();
            MDC.putCloseable(TRX_ID, requestId);
            MDC.putCloseable(PATH_URI, exchange.getRequest().getPath().toString());
            return chain.filter(exchange)
                    .contextWrite(Context.of(TRX_ID, requestId)
                            .put(PATH_URI, exchange.getRequest().getPath().toString()));
        };
    }
}
