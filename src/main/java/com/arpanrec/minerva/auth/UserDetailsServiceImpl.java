package com.arpanrec.minerva.auth;

import com.arpanrec.minerva.exceptions.MinervaException;
import com.arpanrec.minerva.physical.KeyValue;
import com.arpanrec.minerva.physical.KeyValuePersistence;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ObjectMapper mapper = new ObjectMapper();

    @Getter
    private final KeyValuePersistence keyValuePersistence;

    public UserDetailsServiceImpl(@Autowired KeyValuePersistence keyValuePersistence) {
        this.keyValuePersistence = keyValuePersistence;
    }

    public static final String USER_KEY_PREFIX = "internal/users";

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        Optional<KeyValue> userData = keyValuePersistence.get(USER_KEY_PREFIX + "/" + username, 0);
        userData.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        try {
            return mapper.readValue(userData.get().getValue(), User.class);
        } catch (JsonProcessingException e) {
            throw new UsernameNotFoundException("Error while loading user", e);
        }
    }

    public void saveUser(User user) throws MinervaException {
        log.debug("Saving user: {}", user);
        try {
            String userDataString = mapper.writeValueAsString(user);
            KeyValue userData = new KeyValue(USER_KEY_PREFIX + "/" + user.getUsername(), userDataString, false, new HashMap<>(), 0);
            keyValuePersistence.save(userData);
        } catch (Exception e) {
            throw new MinervaException("Error while saving user", e);
        }
    }
}
