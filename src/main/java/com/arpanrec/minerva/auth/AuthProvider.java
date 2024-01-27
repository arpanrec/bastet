package com.arpanrec.minerva.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import com.arpanrec.minerva.hash.Argon2Kt;

@Slf4j
@Component
public class AuthProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AuthImpl auth = (AuthImpl) authentication;
        log.debug("User authentication started for {}", auth.getName());
        if (auth.getCredentials().equals(Argon2Kt.hashString(auth.getProvidedPassword()))) {
            log.info("User {} authenticated", auth.getName());
            auth.setAuthenticated(true);
        } else {
            throw new BadCredentialsException("Wrong password");
        }
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // Return true if this AuthenticationProvider supports the provided authentication class
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
