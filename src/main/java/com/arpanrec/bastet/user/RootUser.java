package com.arpanrec.bastet.minerva.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RootUser implements CommandLineRunner {
    private final UserServiceImpl userServiceImpl;

    private final String rootUsername;

    private final String rootPassword;

    public RootUser(@Autowired UserServiceImpl userServiceImpl,
                    @Value("${minerva.auth.security-config.root-username:root}") String rootUsername,
                    @Value("${minerva.auth.security-config.root-password:root}") String rootPassword) {
        this.userServiceImpl = userServiceImpl;
        this.rootUsername = rootUsername;
        this.rootPassword = rootPassword;
    }

    @Override
    public void run(String... args) {
        Set<UserPrivilege> rootPrivileges = Set.of(new UserPrivilege(PrivilegeTypes.SUDO));
        Set<UserRole> rootRoles = Set.of(new UserRole(RoleTypes.ADMIN, rootPrivileges));
        User rootUser = new User(rootUsername, rootPassword, rootRoles);
        rootUser.setId(1L);
        userServiceImpl.saveOrUpdate(rootUser);
    }
}
