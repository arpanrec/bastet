package com.arpanrec.minerva.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String rootUsername;

    private final String rootPassword;

    private final OncePerRequestFilter authenticationOncePerRequestFilter;

    private final AuthenticationProvider authenticationProvider;

    private final UserDetailsService userDetailsService;

    public SecurityConfig(@Autowired AuthReqFilter authReqFilter, @Autowired AuthProvider authProvider,
                          @Autowired UserDetailsServiceImpl userDetailsServiceImpl,
                          @Value("${minerva.auth.security-config.root-username:root}") String rootUsername,
                          @Value("${minerva.auth.security-config.root-password:root}") String rootPassword) {
        this.authenticationOncePerRequestFilter = authReqFilter;
        this.authenticationProvider = authProvider;
        this.userDetailsService = userDetailsServiceImpl;
        this.rootUsername = rootUsername;
        this.rootPassword = rootPassword;
        doRootUserSetup();
    }

    private RequestMatcher[] getPermitAllRequestMatchers() {
        return new RequestMatcher[]{
            new AntPathRequestMatcher("/h2-console/**"), new AntPathRequestMatcher("/error")
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        http.sessionManagement(
            sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authenticationProvider(authenticationProvider);
        http.addFilterAfter(authenticationOncePerRequestFilter, BasicAuthenticationFilter.class);

        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .requestMatchers(getPermitAllRequestMatchers()).permitAll()

            .requestMatchers(new AntPathRequestMatcher("/api/v1/keyvaule/**"))
            .hasAuthority(AuthUser.Privilege.Type.READ.name())
            // .hasRole(AuthUser.Role.Type.ADMIN.name())

            .anyRequest().authenticated()
        );

        return http.build();
    }

    private void doRootUserSetup() {
        List<AuthUser.Privilege> rootPrivileges = List.of(new AuthUser.Privilege(AuthUser.Privilege.Type.SUDO));
        List<AuthUser.Role> rootRoles = List.of(new AuthUser.Role(AuthUser.Role.Type.ADMIN, rootPrivileges),
            new AuthUser.Role(AuthUser.Role.Type.ANONYMOUS, rootPrivileges),
            new AuthUser.Role(AuthUser.Role.Type.USER, rootPrivileges));
        AuthUser rootUser =
            AuthUser.builder().username(this.rootUsername).password(this.rootPassword).accountNonExpired(true)
            .accountNonLocked(true).credentialsNonExpired(true).enabled(true).roles(rootRoles).build();
        ((UserDetailsServiceImpl) userDetailsService).getKeyValuePersistence()
            .get(((UserDetailsServiceImpl) userDetailsService).getInternalUsersKeyPath() + "/" + rootUsername)
            .ifPresentOrElse((kv) -> log.info("Root user already exists"), () -> {
                try {
                    ((UserDetailsServiceImpl) userDetailsService).saveUser(rootUser);
                    log.info("Root user created");
                } catch (Exception e) {
                    throw new RuntimeException("Error while creating root user", e);
                }
            });
    }
}
