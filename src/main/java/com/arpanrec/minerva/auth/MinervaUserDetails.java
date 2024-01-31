package com.arpanrec.minerva.auth;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MinervaUserDetails extends User {

    @Serial
    private static final long serialVersionUID = 2915242437438173088L;

    @Getter
    @Setter
    private String password;

    Collection<Role> authorities;

    public MinervaUserDetails(@JsonProperty("username") String username, @JsonProperty("password") String password,
                              @JsonProperty("authorities") Collection<Role> authorities) {
        super(username, password, authorities);
        this.authorities = authorities;
    }

    @JsonIgnore
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (this.authorities == null) {
            return authorities;
        }
        for (Role role : this.authorities) {
            authorities.add(role);
            if (role.getPrivileges() == null) {
                continue;
            }
            authorities.addAll(role.getPrivileges());
        }

        return authorities;
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
            ADMIN, USER
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
            SUDO
        }
    }
}
