package com.arpanrec.minerva.auth;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@Setter
@ConfigurationProperties(prefix = "minerva.auth")
public class AuthProperties implements CommandLineRunner {

    @Getter
    private String headerKey = "Authorization";
    private String rootUsername = "root";
    private String rootPassword = "root";

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    public void run(String... args) {
        User rootUser = new User();
        rootUser.setUsername(rootUsername);
        rootUser.setPassword(rootPassword);
        userDetailsServiceImpl.getKeyValuePersistence().get(userDetailsServiceImpl.getInternalUsersKeyPath() + "/" + rootUsername, 0).ifPresentOrElse(
                (kv) -> log.info("Root user already exists"),
                () -> {
                    try {
                        userDetailsServiceImpl.saveUser(rootUser);
                        log.info("Root user created");
                    } catch (Exception e) {
                        throw new RuntimeException("Error while creating root user", e);
                    }
                }
        );
    }
}
