package com.arpanrec.minerva.auth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SquirrelDetailsService implements UserDetailsService {

    private final SquirrelsRepository squirrelsRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        return squirrelsRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
