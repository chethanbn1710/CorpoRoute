package com.corporoute.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {
    
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()

                .requestMatchers("/companies/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT,"/users/me/online")
                .hasRole("DRIVER")

                .requestMatchers(HttpMethod.PUT,"/users/me/offline")
                .hasRole("DRIVER")

                .requestMatchers(HttpMethod.PUT,"/users/me/location")
                .hasRole("DRIVER")

                .requestMatchers("/users/**").hasRole("ADMIN")
                
                .requestMatchers("/rides/my-bookings")
                .hasRole("EMPLOYEE")

                .requestMatchers("/rides/my-assignments")
                .hasRole("DRIVER")

                .requestMatchers(HttpMethod.PUT, "/rides/*/cancel")
                .hasRole("EMPLOYEE")

                .requestMatchers(HttpMethod.GET, "/rides")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/rides/*")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/rides")
                .hasRole("EMPLOYEE")

                .requestMatchers(HttpMethod.PUT, "/rides/*/accept")
                .hasRole("DRIVER")

                .requestMatchers(HttpMethod.PUT, "/rides/*/complete")
                .hasRole("DRIVER")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration configuration) throws Exception {
            return configuration.getAuthenticationManager();
    }
}