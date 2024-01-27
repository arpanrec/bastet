package com.arpanrec.minerva.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.io.Serial;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthImpl implements Authentication {

    @Serial
    private static final long serialVersionUID = -8620294545092862085L;

    private boolean authenticated;
    private User user;
    private String providedPassword;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
