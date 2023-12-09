package com.arpanrec.minerva.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SquirrelsRepository extends JpaRepository<AuthPrincipalUser, Long> {

    // Optional<AuthPrincipalUser> findByEmail(String email);

    Optional<AuthPrincipalUser> findByName(String name);
}
