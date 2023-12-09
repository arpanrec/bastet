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
public class SquirrelAuthenticationOncePerRequestFilter extends OncePerRequestFilter {

    public SquirrelAuthenticationOncePerRequestFilter(
            SquirrelAuthenticationManager squirrelAuthenticationManager,
            SquirrelDetailsService squirrelDetailsService,
            MinervaAuthProperties minervaAuthProperties) {
        this.squirrelAuthenticationManager = squirrelAuthenticationManager;
        this.squirrelDetailsService = squirrelDetailsService;
        this.minervaAuthProperties = minervaAuthProperties;
    }

    private final SquirrelAuthenticationManager squirrelAuthenticationManager;
    private final SquirrelDetailsService squirrelDetailsService;

    private final MinervaAuthProperties minervaAuthProperties;

    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {


        var base64Cred = request.getHeader(minervaAuthProperties.getHeaderKey());
        if (base64Cred == null) {
            filterChain.doFilter(request, response);
            return;
        }
        log.debug("headerKey: {}, base64Cred: {}", minervaAuthProperties.getHeaderKey(), base64Cred);

        String[] creds = base64Cred.split(":");
        String username = creds[0];
        String providedPassword = creds[1];

        Squirrel userDetails = squirrelDetailsService.loadUserByUsername(username);
        SquirrelAuthentication squirrelAuthentication = new SquirrelAuthentication();
        squirrelAuthentication.setProvidedPassword(providedPassword);
        squirrelAuthentication.setSquirrel(userDetails);
        squirrelAuthentication.setAuthenticated(false);

        Authentication authentication =
                squirrelAuthenticationManager.authenticate(squirrelAuthentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
