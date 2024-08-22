package com.arpanrec.minerva.user;

import org.springframework.stereotype.Service;


@Service
public class UserService {



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
                User existingUser = userRepository.findByUsername(user.getUsername());
                user.setId(existingUser.getId());
                userRepository.save(user);
            }
        }
    }
}
