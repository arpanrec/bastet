package com.arpanrec.minerva.auth;

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

    @Override
    public String getAuthority() {
        return name;
    }
}