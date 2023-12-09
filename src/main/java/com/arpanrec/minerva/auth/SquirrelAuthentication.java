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
    private AuthPrincipalUser authPrincipalUser;
    private String providedPassword;


    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public Object getCredentials() {
        return this.authPrincipalUser.getPassword();
    }

    public Object getDetails() {
        return this.authPrincipalUser;
    }

    public Object getPrincipal() {
        return this.authPrincipalUser;
    }

    @Override
    public String getName() {
        return this.authPrincipalUser.getName();
    }

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }
}
