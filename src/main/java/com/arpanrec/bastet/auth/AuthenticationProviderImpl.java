package com.arpanrec.bastet.minerva.auth;

import com.arpanrec.bastet.hash.Argon2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final PasswordEncoder encoder;

    public AuthenticationProviderImpl(@Autowired Argon2 argon2) {
        this.encoder = argon2;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("User authentication started for {}", authentication.getName());
        log.trace("provided password: {}", ((AuthenticationImpl) authentication).getProvidedPassword());
        log.trace("User password: {}", authentication.getCredentials());
        if (
            encoder.matches(
                ((AuthenticationImpl) authentication).getProvidedPassword(),
                (String) authentication.getCredentials()
            )
        ) {
            log.trace("User {} authenticated", authentication.getName());
            authentication.setAuthenticated(true);
        } else {
            throw new BadCredentialsException("Wrong password");
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // Return true if this AuthenticationProvider supports the provided authentication class
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
