package com.arpanrec.minerva.auth;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class User implements UserDetails {

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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (this.roles == null) {
            return authorities;
        }
        for (Role role : this.roles) {
            authorities.add(role);
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Privilege implements GrantedAuthority {

        @Serial
        private static final long serialVersionUID = -1453442487053691797L;

        private String name;

        @JsonIgnore
        @Override
        public String getAuthority() {
            return name;
        }

        public enum Type {
            READ, WRITE, DELETE, SUDO,
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Role implements GrantedAuthority {

        @Serial
        private static final long serialVersionUID = 1425911275852559225L;

        private String name;

        private Collection<Privilege> privileges;

        @JsonIgnore
        @Override
        public String getAuthority() {
            return name;
        }

        public enum Type {
            ROLE_ADMIN, ROLE_USER, ROLE_ANONYMOUS
        }
    }
}
