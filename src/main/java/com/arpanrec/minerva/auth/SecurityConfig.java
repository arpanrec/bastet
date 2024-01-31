package com.arpanrec.minerva.auth;

import com.arpanrec.minerva.state.ApplicationStateManage;
import com.arpanrec.minerva.utils.FileUtils;
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

    private final ApplicationStateManage applicationStateManage;

    public SecurityConfig(@Autowired MinervaOncePerRequestFilter minervaOncePerRequestFilter,
                          @Autowired MinervaAuthenticationProvider minervaAuthenticationProvider,
                          @Autowired MinervaUserDetailsService minervaUserDetailsService,
                          @Autowired ApplicationStateManage applicationStateManage,
                          @Value("${minerva.auth.security-config.root-username:root}") String rootUsername,
                          @Value("${minerva.auth.security-config.root-password:root}") String rootPassword) {
        this.authenticationOncePerRequestFilter = minervaOncePerRequestFilter;
        this.authenticationProvider = minervaAuthenticationProvider;
        this.userDetailsService = minervaUserDetailsService;
        this.applicationStateManage = applicationStateManage;
        this.rootUsername = FileUtils.fileOrString(rootUsername);
        this.rootPassword = FileUtils.fileOrString(rootPassword);
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

            .requestMatchers(new AntPathRequestMatcher("/api/v1/keyvaule/internal/**"))
            .hasAuthority(MinervaUserDetails.Privilege.Type.SUDO.name())

            .anyRequest().authenticated()
        );

        return http.build();
    }

    private void doRootUserSetup() {
        if (applicationStateManage.isRootUserCreated() || applicationStateManage.getRootUserName() != null) {
            log.info("Root user already created");
            return;
        }

        MinervaUserDetailsService minervaUserDetailsService = (MinervaUserDetailsService) userDetailsService;

        List<MinervaUserDetails.Privilege> rootPrivileges =
            List.of(new MinervaUserDetails.Privilege(MinervaUserDetails.Privilege.Type.SUDO));
        List<MinervaUserDetails.Role> rootRoles =
            List.of(new MinervaUserDetails.Role(MinervaUserDetails.Role.Type.ADMIN, rootPrivileges));
        MinervaUserDetails rootUser = new MinervaUserDetails(rootUsername, rootPassword, rootRoles);
        minervaUserDetailsService.getKeyValuePersistence()
            .get(minervaUserDetailsService.getInternalUsersKeyPath() + "/" + rootUsername)
            .ifPresentOrElse((kv) -> log.info("Root user already exists, {}", kv.getValue()), () -> {
                try {
                    minervaUserDetailsService.saveMinervaUserDetails(rootUser);
                    log.info("Root user created, {}", rootUser);
                } catch (Exception e) {
                    throw new RuntimeException("Error while creating root user", e);
                }
            });
        applicationStateManage.setRootUserCreated(true);
        applicationStateManage.setRootUserName(rootUsername);
    }
}
