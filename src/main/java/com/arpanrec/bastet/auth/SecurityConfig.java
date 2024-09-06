package com.arpanrec.bastet.auth;

import com.arpanrec.bastet.exceptions.CaughtException;
import com.arpanrec.bastet.utils.FileUtils;
import jakarta.annotation.PostConstruct;
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


    public SecurityConfig(@Autowired AuthenticationFilter authenticationFilter,
                          @Autowired AuthenticationProviderImpl authenticationProviderImpl,
                          @Autowired UserDetailsServiceImpl userDetailsServiceImpl,
                          @Value("${bastet.auth.security-config.root-username:root}") String rootUsername,
                          @Value("${bastet.auth.security-config.root-password:root}") String rootPassword) throws CaughtException {
        this.authenticationOncePerRequestFilter = authenticationFilter;
        this.authenticationProvider = authenticationProviderImpl;
        this.userDetailsService = userDetailsServiceImpl;
        this.rootUsername = FileUtils.fileOrString(rootUsername);
        this.rootPassword = FileUtils.fileOrString(rootPassword);
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

            .requestMatchers(new AntPathRequestMatcher("/api/v1/keyvaule/internal/**"))
            .hasAuthority(UserDetails.Privilege.Type.SUDO.name())

            .anyRequest().authenticated()
        );

        return http.build();
    }

//    @PostConstruct
//    private void doRootUserSetup() {
//
//        UserDetailsServiceImpl userDetailsServiceImpl = (UserDetailsServiceImpl) this.userDetailsService;
//
//        List<UserDetails.Privilege> rootPrivileges =
//            List.of(new UserDetails.Privilege(UserDetails.Privilege.Type.SUDO));
//        List<UserDetails.Role> rootRoles =
//            List.of(new UserDetails.Role(UserDetails.Role.Type.ADMIN, rootPrivileges));
//        UserDetails rootUser = new UserDetails(rootUsername, rootPassword, rootRoles);
//        userDetailsServiceImpl.getKvDataServiceImpl()
//            .get(userDetailsServiceImpl.getInternalUsersKeyPath() + "/" + rootUsername)
//            .ifPresentOrElse((kv) -> log.info("Root user already exists, {}", kv.getValue()), () -> {
//                try {
//                    userDetailsServiceImpl.saveUserDetails(rootUser);
//                    log.info("Root user created, {}", rootUser);
//                } catch (Exception e) {
//                    throw new RuntimeException("Error while creating root user", e);
//                }
//            });
//    }
}
