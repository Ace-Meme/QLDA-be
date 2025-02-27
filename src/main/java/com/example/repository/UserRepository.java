package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

	boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByEmailVerificationToken(String token);

}
