package com.arpanrec.minerva.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void saveOrUpdate(User user) {
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
}
