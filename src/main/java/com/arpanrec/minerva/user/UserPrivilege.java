package com.arpanrec.minerva.user;

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
}
