package com.task.harbor.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

@Configuration
public class SwaggerRouter {

    @Bean
    public RouterFunction<ServerResponse> swaggerRedirect() {
        return RouterFunctions.route(
            request -> request.path().equals("/swagger-ui.html") || request.path().equals("/swagger-ui"),
            request -> ServerResponse.permanentRedirect(URI.create("/swagger-ui/index.html")).build()
        );
    }
}