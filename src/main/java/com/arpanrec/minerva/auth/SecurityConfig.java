package com.arpanrec.minerva.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String rootUsername;

    private final String rootPassword;

    private final AuthReqFilter authReqFilter;

    private final AuthProvider authProvider;

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public SecurityConfig(@Autowired AuthReqFilter authReqFilter, @Autowired AuthProvider authProvider,
                          @Autowired UserDetailsServiceImpl userDetailsServiceImpl,
                          @Value("${minerva.auth.security-config.root-username:root}") String rootUsername, @Value("${minerva.auth.security-config.root-password:root}") String rootPassword) {
        this.authReqFilter = authReqFilter;
        this.authProvider = authProvider;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.rootUsername = rootUsername;
        this.rootPassword = rootPassword;
        doRootUserSetup();
    }

    private RequestMatcher[] getPermitAllRequestMatchers() {
        return new RequestMatcher[]{
            new AntPathRequestMatcher("/h2-console/**"),
            new AntPathRequestMatcher("/error")};
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        http.sessionManagement(
            sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authenticationProvider(authProvider);
        http.addFilterAfter(authReqFilter, BasicAuthenticationFilter.class);

        http.authorizeHttpRequests(
            authorizeRequests -> authorizeRequests.requestMatchers(getPermitAllRequestMatchers())
                .permitAll());
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .requestMatchers(new AntPathRequestMatcher("/api/v1/keyvalue/internal/**")).hasAuthority(
                User.Role.Type.ROLE_ADMIN.name())
        );
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated());

        // http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers(new AntPathRequestMatcher("/**")).permitAll());

        return http.build();
    }

    private void doRootUserSetup() {
        List<User.Privilege> rootPrivileges = List.of(new User.Privilege(User.Privilege.Type.SUDO.name()));
        List<User.Role> rootRoles = List.of(new User.Role(User.Role.Type.ADMIN.name(), rootPrivileges));
        User rootUser = User.builder().username(this.rootUsername).password(this.rootPassword).accountNonExpired(true)
            .accountNonLocked(true).credentialsNonExpired(true).enabled(true).roles(rootRoles).build();
        userDetailsServiceImpl.getKeyValuePersistence().get(
                userDetailsServiceImpl.getInternalUsersKeyPath() + "/" + rootUsername, 0)
            .ifPresentOrElse(
                (kv) -> log.info("Root user already exists"),
                () -> {
                    try {
                        userDetailsServiceImpl.saveUser(rootUser);
                        log.info("Root user created");
                    } catch (Exception e) {
                        throw new RuntimeException("Error while creating root user", e);
                    }
                });
    }
}
