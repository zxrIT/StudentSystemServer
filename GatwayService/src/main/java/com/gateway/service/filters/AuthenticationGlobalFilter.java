package com.gateway.service.filters;

import com.common.exception.entity.authentication.JWTParsingException;
import com.gateway.service.configuration.AuthenticationProperties;
import com.gateway.service.security.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@SuppressWarnings("all")
public class AuthenticationGlobalFilter implements GlobalFilter, Ordered {
    @Autowired
    private final TokenManager tokenManager;
    private final AuthenticationProperties authenticationProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (isExclude(path) || path.startsWith("/authentication/")) {
            return chain.filter(exchange);
        }
        String token = request.getHeaders().getFirst("Authorization");
        if (token == null || token.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        try {
            String tokenBody = token.substring(7);
            Map<String, String> userDetails = tokenManager.extractUserDetails(tokenBody);
            String roleId = userDetails.get("roleId");
            ServerWebExchange newServerWebExchange =
                    exchange.mutate().request(builder -> {
                        builder.header("userId", userDetails.get("id"));
                        builder.header("roleId", roleId);
                    }).build();
            if (path.startsWith("/selection/")) {
                return chain.filter(newServerWebExchange);
            }
            if ("2".equals(roleId)) {
                return chain.filter(newServerWebExchange);
            }
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        } catch (JWTParsingException jwtParsingException) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private boolean isExclude(String path) {
        for (String excludePath : authenticationProperties.getExcludePaths()) {
            if (antPathMatcher.match(excludePath, path)) {
                return true;
            }
        }
        return false;
    }
}
