package com.repository;

import com.entity.Role;
import com.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);   // login with email
    Optional<User> findByUsername(String username); // login with username

    // 🔹 Check if user exists (helpful during registration)
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<User> findByRoles(Role role);

    // 🔹 Search
    Optional<User> findByEmailOrUsername(String email, String username);

}
