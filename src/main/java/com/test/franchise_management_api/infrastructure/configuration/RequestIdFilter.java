package com.test.franchise_management_api.infrastructure.configuration;

import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestIdFilter implements WebFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestId = getOrCreateRequestId(exchange.getRequest());
        exchange.getResponse().getHeaders().set(REQUEST_ID_HEADER, requestId);

        return chain.filter(exchange)
                .contextWrite(context -> context.put(REQUEST_ID_HEADER, requestId))
                .doFirst(() -> MDC.put(REQUEST_ID_HEADER, requestId))
                .doFinally(signalType -> MDC.remove(REQUEST_ID_HEADER));
    }

    private String getOrCreateRequestId(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(REQUEST_ID_HEADER);
        return header == null || header.isBlank() ? UUID.randomUUID().toString() : header;
    }
}

