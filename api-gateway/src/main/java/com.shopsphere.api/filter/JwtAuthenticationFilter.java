package com.shopsphere.api.filter;

import com.shopsphere.api.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/test"
    );

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Skip auth for public endpoints
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        // Extract Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return sendError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String method = request.getMethod().name();

        if (!jwtUtil.isTokenValid(token)) {
            return sendError(exchange, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        String email = jwtUtil.extractEmail(token);
        String role  = jwtUtil.extractRole(token);

        // Role-based access control
        if (path.startsWith("/admin") && !"ADMIN".equals(role)) {
            return sendError(exchange, HttpStatus.FORBIDDEN, "Access denied");
        }

        if (path.startsWith("/catalog") &&
                (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) &&
                !"ADMIN".equals(role)) {
            return sendError(exchange, HttpStatus.FORBIDDEN, "You do not have permission to perform this action");
        }

        if ((path.startsWith("/order/orders/all") || path.startsWith("/order/orders/admin")) &&
                !"ADMIN".equals(role)) {
            return sendError(exchange, HttpStatus.FORBIDDEN, "Access denied");
        }

        // Mutate request to inject user headers downstream
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Email", email)
                .header("X-User-Role", role)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> sendError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        var buffer = response.bufferFactory()
                .wrap(("{\"error\": \"" + message + "\"}").getBytes());
        return response.writeWith(Mono.just(buffer));
    }
}
