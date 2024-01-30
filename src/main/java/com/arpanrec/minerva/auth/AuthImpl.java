package com.arpanrec.minerva.auth;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.security.auth.Subject;
import java.io.Serial;
import java.util.Collection;

@Data
@Builder
public class AuthImpl implements Authentication {

    @Serial
    private static final long serialVersionUID = -8620294545092862085L;

    private boolean authenticated;
    private UserDetails user;
    private String providedPassword;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    public Object getCredentials() {
        return this.user.getPassword();
    }

    public Object getDetails() {
        return this.user;
    }

    public Object getPrincipal() {
        return this.user;
    }

    @Override
    public String getName() {
        return this.user.getUsername();
    }

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }
}
