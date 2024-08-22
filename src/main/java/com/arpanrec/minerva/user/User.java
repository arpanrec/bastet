package com.arpanrec.minerva.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Set;

@Data
@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails, CredentialsContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean accountNonExpired = false;

    @Column(nullable = false)
    private boolean accountNonLocked = false;

    @Column(nullable = false)
    private boolean credentialsNonExpired = false;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column
    @Convert(converter = GrantedAuthorityConverter.class)
    private Set<UserRole> roles;

    public User(String username, String password, Set<UserRole> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public void setPassword(String password) {
        if (password == null) {
            byte[] values = new byte[124];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(values);
            password = new String(values);
            this.password = password;
            return;
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        this.password = password;
    }

    @JsonIgnore
    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public void eraseCredentials() {
        throw new UnsupportedOperationException("eraseCredentials() is not supported in User entity");
    }
}
