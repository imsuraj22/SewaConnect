package com.config;

import com.entity.Role;
import com.entity.User;
import com.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

@Component
public class AdminBootstrapRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.enabled:false}")
    private boolean enabled;

    @Value("${app.bootstrap.admin.username:admin}")
    private String username;

    @Value("${app.bootstrap.admin.email:admin@local.test}")
    private String email;

    @Value("${app.bootstrap.admin.password:}")
    private String password;

    public AdminBootstrapRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!enabled) {
            return;
        }
        if (!StringUtils.hasText(password)) {
            log.warn("Admin bootstrap enabled, but BOOTSTRAP_ADMIN_PASSWORD is empty. Skipping bootstrap.");
            return;
        }
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            log.info("Admin bootstrap skipped; user already exists for username/email.");
            return;
        }

        User admin = new User();
        admin.setUsername(username.trim());
        admin.setEmail(email.trim());
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRoles(Set.of(Role.ROLE_ADMIN));
        admin.setActive(true);
        userRepository.save(admin);
        log.info("Bootstrap admin user created: {}", admin.getUsername());
    }
}
