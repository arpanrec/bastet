package com.arpanrec.bastet.minerva.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPrivilege implements GrantedAuthority {

    private PrivilegeTypes name;

    @Override
    @JsonIgnore
    public String getAuthority() {
        return name.name();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof UserPrivilege other))
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
