package com.krzywdek19.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {
    private final String URL_PREFIX = "/api/v1/";
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("user-service", r -> r
                        .path(URL_PREFIX+"auth/**", URL_PREFIX+"users/**")
                        .uri("lb://user-service")
                )
                .route("workout-service", r -> r
                        .path(URL_PREFIX+"workouts/**")
                        .uri("lb://workout-service")
                )
                .build();
    }
}
