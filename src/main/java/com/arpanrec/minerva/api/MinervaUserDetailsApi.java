package com.arpanrec.minerva.api;

import com.arpanrec.minerva.auth.MinervaUserDetails;
import com.arpanrec.minerva.auth.MinervaUserDetailsService;
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

    private final MinervaUserDetailsService minervaUserDetailsService;

    public MinervaUserDetailsApi(@Autowired MinervaUserDetailsService minervaUserDetailsService) {
        this.minervaUserDetailsService = minervaUserDetailsService;
    }

    @GetMapping(path = "/{username}", produces = "application/json", consumes = "application/json")
    public HttpEntity<MinervaUserDetails> load(@PathVariable String username) {
        return new ResponseEntity<>(minervaUserDetailsService.loadMinervaUserDetailsByUsername(username),
            HttpStatus.OK);
    }

    @PostMapping(path = "", produces = "application/json", consumes = "application/json")
    public HttpEntity<?> save(@RequestBody MinervaUserDetails user) {
        minervaUserDetailsService.saveMinervaUserDetails(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "", produces = "application/json", consumes = "application/json")
    public HttpEntity<?> update(@RequestBody MinervaUserDetails user) throws MinervaException {
        minervaUserDetailsService.updateMinervaUserDetails(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
