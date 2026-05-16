package com.pm.gateway.filter;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationGatewayFilter implements GlobalFilter, Ordered {
    private final JwtValidationFilter jwtValidationFilter;

    @Override
    @NullMarked
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain  chain) {
        return jwtValidationFilter.filter(exchange).flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
