package com.arpanrec.minerva.api;

import com.arpanrec.minerva.auth.MinervaUserDetails;
import com.arpanrec.minerva.auth.MinervaUserDetailsService;
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

    private final MinervaUserDetailsService minervaUserDetailsService;

    public UsersApi(@Autowired MinervaUserDetailsService minervaUserDetailsService) {
        this.minervaUserDetailsService = minervaUserDetailsService;
    }

    @GetMapping(path = "/api/v1/users/{username}", produces = "application/json", consumes = "application/json")
    public MinervaUserDetails get(@PathVariable String username) throws MinervaException {
        return minervaUserDetailsService.loadMinervaUserDetailsByUsername(username);
    }

    @PostMapping(path = "/api/v1/users", produces = "application/json", consumes = "application/json")
    public void set(@RequestBody MinervaUserDetails user) throws MinervaException {
        minervaUserDetailsService.saveMinervaUserDetails(user);
    }

    @PutMapping(path = "/api/v1/users", produces = "application/json", consumes = "application/json")
    public void update(@RequestBody MinervaUserDetails user) throws MinervaException {
        minervaUserDetailsService.updateMinervaUserDetails(user);
    }

}
