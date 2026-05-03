package com.shopsphere.catalog.config;

import jakarta.ws.rs.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();

//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                .requestMatchers(HttpMethod.GET, "/catalog/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/catalog/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.PUT, "/catalog/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.DELETE, "/catalog/**").hasRole("ADMIN")
//                .anyRequest().authenticated()
//        );
//        return http.build();
    }
}
