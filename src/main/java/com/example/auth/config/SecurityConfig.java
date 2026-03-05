package com.example.auth.config;

import com.example.auth.filter.RateLimitFilter;
import com.example.auth.filter.SecurityHeadersFilter;
import com.example.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitFilter rateLimitFilter;
    private final SecurityHeadersFilter securityHeadersFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // ✅ PUBLIC ENDPOINTS
                        .requestMatchers("/api/auth/register/**").permitAll()
                        .requestMatchers("/api/auth/login/**").permitAll()
                        .requestMatchers("/api/auth/forgot-password/**").permitAll()
                        .requestMatchers("/api/auth/reset-password/**").permitAll()

                        // ✅ ACTUATOR
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // ✅ API DOCUMENTATION
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // ✅ ADMIN ENDPOINTS
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                        // ✅ ROLE BASED
                        .requestMatchers("/api/auth/user/**").hasAuthority("USER")
                        .requestMatchers("/api/auth/owner/**").hasAuthority("OWNER")
                        .requestMatchers("/api/auth/admin/**").hasAuthority("ADMIN")

                        .anyRequest().authenticated()
                )

                // ✅ SECURITY HEADERS FILTER (first)
                .addFilterBefore(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class)

                // ✅ RATE LIMIT FILTER
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)

                // ✅ JWT FILTER
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}