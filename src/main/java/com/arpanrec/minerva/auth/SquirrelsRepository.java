package com.arpanrec.minerva.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SquirrelsRepository extends JpaRepository<Squirrel, Long> {

    Optional<Squirrel> findByEmail(String email);

    Optional<Squirrel> findByName(String name);
}
