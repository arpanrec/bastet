package com.arpanrec.minerva.auth;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser implements UserDetails, CredentialsContainer {

    @Serial
    private static final long serialVersionUID = 2915242437438173088L;

    private String username;

    private String password;

    private String email;

    private List<Role> roles;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (this.roles == null) {
            return authorities;
        }
        for (Role role : this.roles) {
            authorities.add(role);
            if (role.getPrivileges() == null) {
                continue;
            }
            authorities.addAll(role.getPrivileges());
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void eraseCredentials() {

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Role implements GrantedAuthority {

        @Serial
        private static final long serialVersionUID = 1425911275852559225L;

        private Type name;

        private Collection<Privilege> privileges;

        @JsonIgnore
        @Override
        public String getAuthority() {
            return "ROLE_" + name.toString();
        }

        public enum Type {
            // ADMIN, USER, ANONYMOUS,
            ADMIN, USER, ANONYMOUS,
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Privilege implements GrantedAuthority {

        @Serial
        private static final long serialVersionUID = -1453442487053691797L;

        private Type name;

        @Override
        @JsonIgnore
        public String getAuthority() {
            return name.toString();
        }

        public enum Type {
            READ, WRITE, DELETE, SUDO,
        }
    }
}
