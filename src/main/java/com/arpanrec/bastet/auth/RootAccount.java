package com.arpanrec.bastet.auth;

import com.arpanrec.bastet.exceptions.CaughtException;
import com.arpanrec.bastet.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Slf4j
public class RootAccount {
    private final String rootUsername;

    private final String rootPassword;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public RootAccount(
        @Autowired UserDetailsServiceImpl userDetailsServiceImpl,
        @Value("${bastet.auth.security-config.root-username:root}") String rootUsername,
        @Value("${bastet.auth.security-config.root-password:root}") String rootPassword) throws CaughtException {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.rootUsername = FileUtils.fileOrString(rootUsername);
        this.rootPassword = FileUtils.fileOrString(rootPassword);
    }

    public void doRootUserSetup() throws CaughtException {
        List<UserDetails.Privilege> rootPrivileges =
            List.of(new UserDetails.Privilege(UserDetails.Privilege.Type.SUDO));
        List<UserDetails.Role> rootRoles =
            List.of(new UserDetails.Role(UserDetails.Role.Type.ADMIN, rootPrivileges));
        UserDetails rootUser = new UserDetails(rootUsername, rootPassword, rootRoles);
        userDetailsServiceImpl.saveUserDetails(rootUser);
    }
}
