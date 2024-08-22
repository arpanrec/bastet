package com.arpanrec.minerva.user;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;
import java.util.Set;

@Data
@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

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

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username) {
        this.username = username;
        Random random = new Random();
        this.password = String.valueOf(random.nextInt(1000000));
    }
}
