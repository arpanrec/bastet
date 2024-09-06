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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Getter
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final KVDataService kvDataService;

    private final String internalUsersKeyPath = NameSpace.USERS;

    private final PasswordEncoder encoder;

    private final ObjectMapper objectMapper;

    public UserDetailsServiceImpl(@Autowired KVDataService kvDataService, @Autowired Argon2 argon2) {
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

    public UserDetails loadMinervaUserDetailsByUsername(String username) throws MinervaException {
        log.debug("Loading user by username: {}", username);
        Optional<KVData> userData = kvDataService.get(internalUsersKeyPath + "/" + username);
        if (userData.isEmpty()) {
            throw new MinervaException("User not found with username: " + username);
        } else {
            try {
                UserDetails userDetails = objectMapper.readValue(userData.get().getValue(),
                    UserDetails.class);
                log.trace("User loaded: {}", userDetails);
                return userDetails;
            } catch (Exception e) {
                throw new MinervaException("Error while loading user", e);
            }
        }
    }

    private KVData minervaUserDetailsToKeyValue(UserDetails userDetails) throws MinervaException {
        String hashedPassword = encoder.encode(userDetails.getPassword());
        userDetails = new UserDetails(
            userDetails.getUsername(),
            hashedPassword,
            userDetails.getRoles());
        log.debug("User password hashed: {}", userDetails);
        try {
            return new KVData(
                NameSpace.USERS + "/" + userDetails.getUsername(),
                objectMapper.writeValueAsString(userDetails),
                new HashMap<>()
            );
        } catch (Exception e) {
            throw new MinervaException("Error while saving user", e);
        }
    }

    public void saveMinervaUserDetails(UserDetails userDetails) throws MinervaException {
        log.debug("Saving user: {}", userDetails.toString());
        KVData userData = minervaUserDetailsToKeyValue(userDetails);
        kvDataService.saveOrUpdate(userData);
    }

    public void updateMinervaUserDetails(UserDetails userDetails) throws MinervaException {
        log.debug("Updating user: {}", userDetails.toString());
        KVData userData = minervaUserDetailsToKeyValue(userDetails);
        kvDataService.saveOrUpdate(userData);
    }
}
