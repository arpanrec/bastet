package com.arpanrec.minerva.auth;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Builder
public class User implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1003772465463552427L;

    @Setter
    private Long id;

    @Setter
    private String name;

    @Getter
    private String password;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private List<Role> roles;

    @Getter
    @Setter
    private boolean accountNonExpired;

    @Getter
    @Setter
    private boolean accountNonLocked;

    @Getter
    @Setter
    private boolean credentialsNonExpired;

    @Getter
    @Setter
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

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getUsername() {
        return this.name;
    }

    public record Role(Long id, String name, Collection<Privilege> privileges) implements GrantedAuthority, Serializable {

        public Role {
            if (privileges == null) {
                privileges = new ArrayList<>();
            }
        }

        @Serial
        private static final long serialVersionUID = 1003772487463552427L;

        @Override
        public String getAuthority() {
            return name;
        }
    }

    public record Privilege(Long id, String name) implements GrantedAuthority, Serializable {
        @Serial
        private static final long serialVersionUID = 1003772487463552427L;

        @Override
        public String getAuthority() {
            return name;
        }
    }
}
