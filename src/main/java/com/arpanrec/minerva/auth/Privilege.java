package com.arpanrec.minerva.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Privilege implements GrantedAuthority {

    @Serial
    private static final long serialVersionUID = -1453442487053691797L;

    private String name;

    @JsonIgnore
    @Override
    public String getAuthority() {
        return name;
    }

    public enum PrivilegeEnum {
        READ,
        WRITE,
        DELETE,
        SUDO,
    }
}
