package com.arpanrec.minerva.auth;

import com.arpanrec.minerva.exceptions.MinervaException;
import com.arpanrec.minerva.hash.Argon2;
import com.arpanrec.minerva.physical.KVData;
import com.arpanrec.minerva.physical.KVDataService;
import com.arpanrec.minerva.physical.NameSpace;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Getter
@Slf4j
@Service
public class MinervaUserDetailsService implements UserDetailsService {

    private final KVDataService kvDataService;

    private final String internalUsersKeyPath = NameSpace.USERS;

    private final PasswordEncoder encoder;

    private final ObjectMapper objectMapper;

    public MinervaUserDetailsService(@Autowired KVDataService kvDataService, @Autowired Argon2 argon2) {
        this.encoder = argon2;
        this.kvDataService = kvDataService;
        objectMapper = new ObjectMapper();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return loadMinervaUserDetailsByUsername(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found", e);
        }
    }

    public MinervaUserDetails loadMinervaUserDetailsByUsername(String username) throws MinervaException {
        log.debug("Loading user by username: {}", username);
        Optional<KVData> userData = kvDataService.get(internalUsersKeyPath + "/" + username);
        if (userData.isEmpty()) {
            throw new MinervaException("User not found with username: " + username);
        } else {
            try {
                MinervaUserDetails minervaUserDetails = objectMapper.readValue(userData.get().getValue(),
                    MinervaUserDetails.class);
                log.trace("User loaded: {}", minervaUserDetails);
                return minervaUserDetails;
            } catch (Exception e) {
                throw new MinervaException("Error while loading user", e);
            }
        }
    }

    private KVData minervaUserDetailsToKeyValue(MinervaUserDetails minervaUserDetails) throws MinervaException {
        String hashedPassword = encoder.encode(minervaUserDetails.getPassword());
        minervaUserDetails = new MinervaUserDetails(
            minervaUserDetails.getUsername(),
            hashedPassword,
            minervaUserDetails.getRoles());
        log.debug("User password hashed: {}", minervaUserDetails);
        try {
            return new KVData(
                NameSpace.USERS + "/" + minervaUserDetails.getUsername(),
                objectMapper.writeValueAsString(minervaUserDetails),
                new HashMap<>()
            );
        } catch (Exception e) {
            throw new MinervaException("Error while saving user", e);
        }
    }

    public void saveMinervaUserDetails(MinervaUserDetails minervaUserDetails) throws MinervaException {
        log.debug("Saving user: {}", minervaUserDetails.toString());
        KVData userData = minervaUserDetailsToKeyValue(minervaUserDetails);
        kvDataService.saveOrUpdate(userData);
    }

    public void updateMinervaUserDetails(MinervaUserDetails minervaUserDetails) throws MinervaException {
        log.debug("Updating user: {}", minervaUserDetails.toString());
        KVData userData = minervaUserDetailsToKeyValue(minervaUserDetails);
        kvDataService.saveOrUpdate(userData);
    }
}
