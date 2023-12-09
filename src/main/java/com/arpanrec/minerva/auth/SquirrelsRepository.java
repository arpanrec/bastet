package com.arpanrec.minerva.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SquirrelsRepository extends JpaRepository<User, Long> {

    // Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);
}
