package com.arpanrec.minerva.api;

import com.arpanrec.minerva.auth.AuthUser;
import com.arpanrec.minerva.auth.UserDetailsServiceImpl;
import com.arpanrec.minerva.exceptions.MinervaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersApi {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public UsersApi(@Autowired UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @GetMapping(path = "/api/v1/users/{username}", produces = "application/json", consumes = "application/json")
    public AuthUser get(@PathVariable String username) throws MinervaException {
        return userDetailsServiceImpl.loadAuthUserByUsername(username);
    }

    @PostMapping(path = "/api/v1/users", produces = "application/json", consumes = "application/json")
    public void set(@RequestBody AuthUser user) throws MinervaException {
        userDetailsServiceImpl.saveUser(user);
    }

    @PutMapping(path = "/api/v1/users", produces = "application/json", consumes = "application/json")
    public void update(@RequestBody AuthUser user) throws MinervaException {
        userDetailsServiceImpl.updateUser(user);
    }

}
