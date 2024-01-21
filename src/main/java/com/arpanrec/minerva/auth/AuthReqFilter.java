package com.arpanrec.minerva.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class AuthReqFilter extends OncePerRequestFilter {

    public AuthReqFilter(AuthManager authManager, UserDetailsServiceImpl userDetailsServiceImpl, AuthProperties authProperties) {
        this.authManager = authManager;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.authProperties = authProperties;
    }

    private final AuthManager authManager;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    private final AuthProperties authProperties;

    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {


        var base64Cred = request.getHeader(authProperties.getHeaderKey());
        if (base64Cred == null) {
            filterChain.doFilter(request, response);
            return;
        }
        log.debug("headerKey: {}, base64Cred: {}", authProperties.getHeaderKey(), base64Cred);

        String[] credential = base64Cred.split(":");
        String username = credential[0];
        String providedPassword = credential[1];

        User user = userDetailsServiceImpl.loadUserByUsername(username);
        AuthImpl authImpl = new AuthImpl();
        authImpl.setProvidedPassword(providedPassword);
        authImpl.setUser(user);
        authImpl.setAuthenticated(false);

        Authentication authentication = authManager.authenticate(authImpl);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
