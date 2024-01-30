package com.arpanrec.minerva.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class MinervaAuthenticationManager implements AuthenticationManager {

    private final AuthenticationProvider authenticationProvider;

    public MinervaAuthenticationManager(@Autowired MinervaAuthenticationProvider minervaAuthenticationProvider) {
        this.authenticationProvider = minervaAuthenticationProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return authenticationProvider.authenticate(authentication);
    }
}
