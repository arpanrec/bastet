package com.arpanrec.minerva.auth;

import com.arpanrec.minerva.exceptions.MinervaException;
import com.arpanrec.minerva.hash.Argon2;
import com.arpanrec.minerva.physical.KeyValue;
import com.arpanrec.minerva.physical.KeyValuePersistence;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Getter
@Slf4j
@Service
public class MinervaUserDetailsService implements UserDetailsService {

    private final KeyValuePersistence keyValuePersistence;

    private final String internalUsersKeyPath;

    private final PasswordEncoder encoder;

    public MinervaUserDetailsService(@Autowired KeyValuePersistence keyValuePersistence, @Autowired Argon2 argon2) {
        this.encoder = argon2;
        this.keyValuePersistence = keyValuePersistence;
        internalUsersKeyPath = keyValuePersistence.getInternalStorageKey() + "/users";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return loadMinervaUserDetailsByUsername(username);
        } catch (MinervaException e) {
            throw new UsernameNotFoundException("User not found", e);
        }
    }

    public MinervaUserDetails loadMinervaUserDetailsByUsername(String username) throws MinervaException {
        log.debug("Loading user by username: {}", username);
        Optional<KeyValue> userData = keyValuePersistence.get(internalUsersKeyPath + "/" + username, 0);
        userData.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        byte[] data = Base64.getDecoder().decode(Objects.requireNonNull(userData.get().getValue()).getBytes());
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)
        ) {
            return (MinervaUserDetails) objectInputStream.readObject();
        } catch (Exception e) {
            throw new MinervaException("Error while loading user", e);
        }
    }

    private KeyValue minervaUserDetailsToKeyValue(MinervaUserDetails minervaUserDetails) throws MinervaException {
        String hashedPassword = encoder.encode(minervaUserDetails.getPassword());
        minervaUserDetails.setPassword(hashedPassword);
        log.debug("User password hashed: {}", minervaUserDetails);
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
        ) {
            objectOutputStream.writeObject(minervaUserDetails);
            objectOutputStream.flush();
            KeyValue userData = new KeyValue();
            userData.setKey(internalUsersKeyPath + "/" + minervaUserDetails.getUsername());
            userData.setValue(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
            return userData;
        } catch (Exception e) {
            throw new MinervaException("Error while saving user", e);
        }
    }

    public void saveUser(MinervaUserDetails minervaUserDetails) throws MinervaException {
        log.debug("Saving user: {}", minervaUserDetails.toString());
        KeyValue userData = minervaUserDetailsToKeyValue(minervaUserDetails);
        keyValuePersistence.save(userData);
    }

    public void updateUser(MinervaUserDetails minervaUserDetails) throws MinervaException {
        log.debug("Updating user: {}", minervaUserDetails.toString());
        KeyValue userData = minervaUserDetailsToKeyValue(minervaUserDetails);
        keyValuePersistence.update(userData);
    }
}
