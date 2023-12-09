package com.arpanrec.minerva.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthManager implements AuthenticationManager {

    private final AuthProvider authProvider;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        return authProvider.authenticate(authentication);
    }
}
