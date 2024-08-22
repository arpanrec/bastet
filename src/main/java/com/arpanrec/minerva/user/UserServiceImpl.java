package com.arpanrec.minerva.user;

import com.arpanrec.minerva.hash.Argon2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserDetailsService {


    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    public UserServiceImpl(@Autowired UserRepository userRepository,
                           @Autowired Argon2 argon2) {
        this.userRepository = userRepository;
        this.encoder = argon2;
    }

    public void save(User user) {
        encodePassword(user);
        userRepository.save(user);
    }

    public void saveOrUpdate(User user) {
        encodePassword(user);
        if (!userRepository.existsByUsername(user.getUsername())) {
            userRepository.save(user);
        } else {
            if (user.getId() != null) {
                userRepository.save(user);
            } else {
                Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
                user.setId(existingUser.orElseThrow().getId());
                userRepository.save(user);
            }
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> existingUser = userRepository.findByUsername(username);
        return existingUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private void encodePassword(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
    }
}
