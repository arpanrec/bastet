package com.arpanrec.minerva.auth;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@Data
@ConfigurationProperties(prefix = "minerva.auth")
public class AuthProperties implements CommandLineRunner {

    private String headerKey = "Authorization";
    private String rootUsername = "root";
    private String rootPassword = "root";

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;


    @Override
    public void run(String... args) {
        User rootUser = new User(0L, rootUsername, rootPassword, "", null, true, true, true, true);
        userDetailsServiceImpl.getKeyValuePersistence().get(UserDetailsServiceImpl.USER_KEY_PREFIX + "/" + rootUsername, 0).ifPresentOrElse(
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
