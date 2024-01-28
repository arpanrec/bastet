package com.arpanrec.minerva.auth;


import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
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

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (this.roles == null) {
            return authorities;
        }
        for (Role role : this.roles) {
            authorities.add(role);
            authorities.addAll(role.privileges);
        }

        return authorities;
    }

    public record Role(String name, Collection<Privilege> privileges) implements GrantedAuthority {

        public Role {
            if (privileges == null) {
                privileges = new ArrayList<>();
            }
        }

        @Override
        public String getAuthority() {
            return name;
        }
    }

    public record Privilege(String name) implements GrantedAuthority {

        @Override
        public String getAuthority() {
            return name;
        }
    }
}
