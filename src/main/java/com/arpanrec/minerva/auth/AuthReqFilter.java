package com.arpanrec.minerva.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@Component
public class AuthReqFilter extends OncePerRequestFilter {

    private final String headerKey;
    private final AuthManager authManager;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public AuthReqFilter(@Autowired AuthManager authManager,
                         @Autowired UserDetailsServiceImpl userDetailsServiceImpl,
                         @Value("${minerva.auth.filter.header-key:Authorization}") String headerKey
    ) {
        this.headerKey = headerKey;
        this.authManager = authManager;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        var base64Cred = request.getHeader(headerKey);
        if (base64Cred == null) {
            filterChain.doFilter(request, response);
            return;
        }
        log.debug("headerKey: {}, base64Cred: {}", headerKey, base64Cred);

        String[] credential = new String(Base64.getDecoder().decode(base64Cred.substring(6))).split(":");
        String username = credential[0];
        String providedPassword = credential[1];

        User user = userDetailsServiceImpl.loadUserByUsername(username);

        AuthImpl authImpl = AuthImpl.builder().providedPassword(providedPassword).user(user).authenticated(false).build();

        Authentication authentication = authManager.authenticate(authImpl);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
