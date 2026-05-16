package com.pm.gateway.filter;

import com.pm.gateway.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtValidationFilter {
    private final JwtService jwtService;

    public Mono<ServerWebExchange> filter(ServerWebExchange exchange) {
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("accessToken");

        if (cookie == null) {
            return Mono.just(exchange);
        }

        try {
            String accessToken = cookie.getValue();
            Claims claims = jwtService.validateToken(accessToken);
            String email = claims.getSubject();
            String userId = claims.get("userId", String.class);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    AuthorityUtils.NO_AUTHORITIES
            );

            ServerHttpRequest mutatedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Email", email)
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

            return Mono.just(mutatedExchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        } catch (Exception e) {
            return Mono.just(exchange);
        }
    }
}
