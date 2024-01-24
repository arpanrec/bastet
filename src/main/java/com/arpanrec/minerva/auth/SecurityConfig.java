package com.arpanrec.minerva.auth;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final AuthReqFilter authReqFilter;

    private final AuthProvider authProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(
                headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        http.sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authenticationProvider(authProvider);
        http.addFilterAfter(authReqFilter, BasicAuthenticationFilter.class);
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll());
        http.authorizeHttpRequests(
                authorizeRequests -> authorizeRequests.anyRequest().authenticated());

        return http.build();
    }

}
