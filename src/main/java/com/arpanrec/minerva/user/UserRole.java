package com.arpanrec.minerva.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRole implements GrantedAuthority {

    private RoleTypes name;

    private Set<UserPrivilege> privileges;

    @Override
    @JsonIgnore
    public String getAuthority() {
        String authorityString = name.name();
        return authorityString.startsWith("ROLE_") ? authorityString : "ROLE_" + authorityString;
    }
}
