package com.arpanrec.minerva.auth;

import com.arpanrec.minerva.exceptions.MinervaException;
import com.arpanrec.minerva.hash.Argon2;
import com.arpanrec.minerva.physical.KeyValue;
import com.arpanrec.minerva.physical.KeyValuePersistence;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Getter
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final KeyValuePersistence keyValuePersistence;

    private final String internalUsersKeyPath;

    private final Argon2 argon2;

    public UserDetailsServiceImpl(@Autowired KeyValuePersistence keyValuePersistence, @Autowired Argon2 argon2) {
        this.argon2 = argon2;
        this.keyValuePersistence = keyValuePersistence;
        internalUsersKeyPath = keyValuePersistence.getInternalStorageKey() + "/users";
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        Optional<KeyValue> userData = keyValuePersistence.get(internalUsersKeyPath + "/" + username, 0);
        userData.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        byte[] data = Base64.getDecoder().decode(Objects.requireNonNull(userData.get().getValue()).getBytes());
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (User) objectInputStream.readObject();
        } catch (IOException e) {
            throw new UsernameNotFoundException("Error while loading user", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private KeyValue encryptPasswordAndGetKeyValue(User user) throws MinervaException {
        user.setPassword(argon2.hashString(user.getPassword()));
        log.debug("User password hashed: {}", user);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(user);
            objectOutputStream.flush();
            KeyValue userData = new KeyValue();
            userData.setKey(internalUsersKeyPath + "/" + user.getUsername());
            userData.setValue(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
            return userData;
        } catch (Exception e) {
            throw new MinervaException("Error while saving user", e);
        }
    }

    public void saveUser(User user) throws MinervaException {
        log.debug("Saving user: {}", user.toString());
        KeyValue userData = encryptPasswordAndGetKeyValue(user);
        keyValuePersistence.save(userData);
    }

    public void updateUser(User user) throws MinervaException {
        log.debug("Updating user: {}", user.toString());
        KeyValue userData = encryptPasswordAndGetKeyValue(user);
        keyValuePersistence.update(userData);
    }
}
