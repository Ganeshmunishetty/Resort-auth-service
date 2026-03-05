package com.example.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed.origins:http://localhost:5173,http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // ✅ Configure allowed origins from environment
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));

        // ✅ Allowed methods
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        // ✅ Allowed headers (Authorization is critical for JWT)
        config.setAllowedHeaders(List.of("*"));

        // ✅ Expose headers
        config.setExposedHeaders(List.of("X-Rate-Limit-Remaining", "X-Rate-Limit-Retry-After-Seconds"));

        // ✅ Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // ✅ Cache preflight response for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
