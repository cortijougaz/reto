package com.ibk.in.adapter.config;

import com.ibk.in.adapter.dto.HeaderRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestHeadersArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(HeaderRequest.class);
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
        var headers = exchange.getRequest().getHeaders();

        HeaderRequest headerRequest = new HeaderRequest(
                headers.getFirst("consumerId"),
                headers.getFirst("traceparent"),
                headers.getFirst("deviceType"),
                headers.getFirst("deviceId")
        );

        return Mono.just(headerRequest);
    }
}
