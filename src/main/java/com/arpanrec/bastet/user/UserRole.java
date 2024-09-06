package com.arpanrec.bastet.user;

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

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UserRole other))
            return false;
        return name.equals(other.name);
    }

    @Override
    public final int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name.name();
    }
}
