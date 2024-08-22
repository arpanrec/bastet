package com.arpanrec.minerva.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RootUser implements CommandLineRunner {
    private final UserService userService;

    private final String rootUsername;

    private final String rootPassword;

    public RootUser(@Autowired UserService userService,
                    @Value("${minerva.auth.security-config.root-username:root}") String rootUsername,
                    @Value("${minerva.auth.security-config.root-password:root}") String rootPassword) {
        this.userService = userService;
        this.rootUsername = rootUsername;
        this.rootPassword = rootPassword;
    }

    @Override
    public void run(String... args) {
        Set<UserPrivilege> rootPrivileges = Set.of(new UserPrivilege(PrivilegeTypes.SUDO));
        Set<UserRole> rootRoles = Set.of(new UserRole(RoleTypes.ADMIN, rootPrivileges));
        User rootUser = new User(rootUsername, rootPassword, rootRoles);
        userService.save(rootUser);
    }
}
