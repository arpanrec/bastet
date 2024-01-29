package com.arpanrec.minerva.auth;

import com.arpanrec.minerva.hash.Argon2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthProvider implements AuthenticationProvider {

    private final Argon2 argon2;

    public AuthProvider(@Autowired Argon2 argon2) {
        this.argon2 = argon2;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        AuthImpl auth = (AuthImpl) authentication;
        log.debug("User authentication started for {}", auth.getName());
        String hashedProvidedPassword = argon2.hashString(auth.getProvidedPassword());
        log.debug("Hashed provided password: {}", hashedProvidedPassword);
        log.debug("User password: {}", auth.getCredentials());
        if (auth.getCredentials().equals(hashedProvidedPassword)) {
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
