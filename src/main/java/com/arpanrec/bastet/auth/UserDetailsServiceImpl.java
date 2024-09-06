package com.arpanrec.bastet.auth;

import com.arpanrec.bastet.exceptions.CaughtException;
import com.arpanrec.bastet.hash.Argon2;
import com.arpanrec.bastet.physical.KVData;
import com.arpanrec.bastet.physical.jpa.KVDataServiceImpl;
import com.arpanrec.bastet.physical.NameSpace;
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

    private final KVDataServiceImpl kvDataServiceImpl;

    private final String internalUsersKeyPath = NameSpace.USERS;

    private final PasswordEncoder encoder;

    private final ObjectMapper objectMapper;

    public UserDetailsServiceImpl(@Autowired KVDataServiceImpl kvDataServiceImpl, @Autowired Argon2 argon2) {
        this.encoder = argon2;
        this.kvDataServiceImpl = kvDataServiceImpl;
        objectMapper = new ObjectMapper();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return loadUserDetailsByUsername(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found", e);
        }
    }

    public UserDetails loadUserDetailsByUsername(String username) throws CaughtException {
        log.debug("Loading user by username: {}", username);
        Optional<KVData> userData = kvDataServiceImpl.get(internalUsersKeyPath + "/" + username);
        if (userData.isEmpty()) {
            throw new CaughtException("User not found with username: " + username);
        } else {
            try {
                UserDetails userDetails = objectMapper.readValue(userData.get().getValue(),
                    UserDetails.class);
                log.trace("User loaded: {}", userDetails);
                return userDetails;
            } catch (Exception e) {
                throw new CaughtException("Error while loading user", e);
            }
        }
    }

    private KVData userDetailsToKeyValue(UserDetails userDetails) throws CaughtException {
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
            throw new CaughtException("Error while saving user", e);
        }
    }

    public void saveUserDetails(UserDetails userDetails) throws CaughtException {
        log.debug("Saving user: {}", userDetails.toString());
        KVData userData = userDetailsToKeyValue(userDetails);
        kvDataServiceImpl.saveOrUpdate(userData);
    }

    public void updateUserDetails(UserDetails userDetails) throws CaughtException {
        log.debug("Updating user: {}", userDetails.toString());
        KVData userData = userDetailsToKeyValue(userDetails);
        kvDataServiceImpl.saveOrUpdate(userData);
    }
}
