package com.repository;

import com.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);   // login with email
    Optional<User> findByUsername(String username); // login with username

    // 🔹 Check if user exists (helpful during registration)
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // 🔹 Search
    Optional<User> findByEmailOrUsername(String email, String username);

}
