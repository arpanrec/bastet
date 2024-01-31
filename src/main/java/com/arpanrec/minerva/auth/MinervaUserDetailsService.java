package com.arpanrec.minerva.auth;

import com.arpanrec.minerva.exceptions.MinervaException;
import com.arpanrec.minerva.hash.Argon2;
import com.arpanrec.minerva.physical.KeyValue;
import com.arpanrec.minerva.physical.KeyValuePersistence;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Getter
@Slf4j
@Service
public class MinervaUserDetailsService implements UserDetailsService {

    private final KeyValuePersistence keyValuePersistence;

    private final String internalUsersKeyPath;

    private final PasswordEncoder encoder;

    private final ObjectMapper objectMapper;

    public MinervaUserDetailsService(@Autowired KeyValuePersistence keyValuePersistence, @Autowired Argon2 argon2) {
        this.encoder = argon2;
        this.keyValuePersistence = keyValuePersistence;
        internalUsersKeyPath = keyValuePersistence.getInternalStorageKey() + "/users";
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

    public MinervaUserDetails loadMinervaUserDetailsByUsername(String username) {
        log.debug("Loading user by username: {}", username);
        Optional<KeyValue> userData = keyValuePersistence.get(internalUsersKeyPath + "/" + username);
        if (userData.isEmpty()) {
            throw new MinervaException("User not found with username: " + username);
        } else {
            try {
                MinervaUserDetails minervaUserDetails = objectMapper.readValue(userData.get().getValue(),
                    MinervaUserDetails.class);
                log.debug("User loaded: {}", minervaUserDetails);
                return minervaUserDetails;
            } catch (Exception e) {
                throw new MinervaException("Error while loading user", e);
            }
        }
    }

    private KeyValue minervaUserDetailsToKeyValue(MinervaUserDetails minervaUserDetails) {
        String hashedPassword = encoder.encode(minervaUserDetails.getPassword());
        minervaUserDetails = new MinervaUserDetails(
            minervaUserDetails.getUsername(),
            hashedPassword,
            minervaUserDetails.getRoles());
        log.debug("User password hashed: {}", minervaUserDetails);
        try {
            KeyValue userData = new KeyValue();
            userData.setKey(internalUsersKeyPath + "/" + minervaUserDetails.getUsername());
            userData.setValue(objectMapper.writeValueAsString(minervaUserDetails));
            return userData;
        } catch (Exception e) {
            throw new MinervaException("Error while saving user", e);
        }
    }

    public void saveMinervaUserDetails(MinervaUserDetails minervaUserDetails) {
        log.debug("Saving user: {}", minervaUserDetails.toString());
        KeyValue userData = minervaUserDetailsToKeyValue(minervaUserDetails);
        keyValuePersistence.save(userData);
    }

    public void updateMinervaUserDetails(MinervaUserDetails minervaUserDetails) {
        log.debug("Updating user: {}", minervaUserDetails.toString());
        KeyValue userData = minervaUserDetailsToKeyValue(minervaUserDetails);
        keyValuePersistence.update(userData);
    }
}
