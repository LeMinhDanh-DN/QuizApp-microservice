package com.mr_n.questionservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private HeaderAuthFilter headerAuthenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/question/all").hasRole("ADMIN")
                        .requestMatchers("/question/add").hasRole("ADMIN")
                        .requestMatchers("/question/category/{type}").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(headerAuthenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
