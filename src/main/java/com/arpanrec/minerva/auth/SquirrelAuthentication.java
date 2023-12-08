package com.arpanrec.minerva.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SquirrelAuthentication implements Authentication {

    private boolean authenticated;
    private Squirrel squirrel;
    private String providedPassword;


    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public Object getCredentials() {
        return this.squirrel.getPassword();
    }

    public Object getDetails() {
        return this.squirrel;
    }

    public Object getPrincipal() {
        return this.squirrel;
    }

    @Override
    public String getName() {
        return this.squirrel.getName();
    }

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }
}
