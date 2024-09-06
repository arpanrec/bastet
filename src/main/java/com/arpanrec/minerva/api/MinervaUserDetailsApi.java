package com.arpanrec.minerva.api;

import com.arpanrec.minerva.auth.UserDetails;
import com.arpanrec.minerva.auth.UserDetailsServiceImpl;
import com.arpanrec.minerva.exceptions.MinervaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class MinervaUserDetailsApi {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public MinervaUserDetailsApi(@Autowired UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @GetMapping(path = "/{username}", produces = "application/json", consumes = "application/json")
    public HttpEntity<?> load(@PathVariable String username) {
        return new ResponseEntity<>(userDetailsServiceImpl.loadUserByUsername(username), HttpStatus.OK);
    }

    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public HttpEntity<?> save(@RequestBody UserDetails user) throws MinervaException {
        userDetailsServiceImpl.saveMinervaUserDetails(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "", produces = "application/json", consumes = "application/json")
    public HttpEntity<?> update(@RequestBody UserDetails user) throws MinervaException {
        userDetailsServiceImpl.updateMinervaUserDetails(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
