package com.service;

import com.dto.AuthResponse;
import com.dto.LoginRequest;
import com.dto.RegisterRequest;
import com.dto.UserDTO;
import com.entity.Role;
import com.entity.User;
import com.ngo.security.JwtProperties;
import com.ngo.security.JwtTokenService;
import com.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final NgoServiceClient ngoServiceClient;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties,
            AuthenticationManager authenticationManager,
            UserService userService,
            NgoServiceClient ngoServiceClient
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.ngoServiceClient = ngoServiceClient;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        Set<Role> roles = resolveRoles(request.getRoles());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);

        User saved = userRepository.save(user);

        if (saved.getRoles().contains(Role.ROLE_NGO)) {
            try {
                ngoServiceClient.registerNgoStub(saved.getId());
            } catch (Exception ex) {
                log.warn(
                        "NGO stub could not be created at signup for user {} ({}). "
                                + "It will be created when the user opens NGO workspace: {}",
                        saved.getId(),
                        saved.getUsername(),
                        ex.getMessage()
                );
            }
        }

        return buildAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));
        return buildAuthResponse(user);
    }

    private Set<Role> resolveRoles(Set<String> roleStrings) {
        Set<Role> roles = new HashSet<>();
        if (roleStrings == null || roleStrings.isEmpty()) {
            roles.add(Role.ROLE_DONOR);
            return roles;
        }
        for (String raw : roleStrings) {
            String norm = raw.trim().toUpperCase();
            if (!norm.startsWith("ROLE_")) {
                norm = "ROLE_" + norm;
            }
            if (Role.ROLE_ADMIN.name().equals(norm)) {
                throw new IllegalArgumentException("Self-registration as admin is not allowed");
            }
            roles.add(Role.valueOf(norm));
        }
        return roles;
    }

    private AuthResponse buildAuthResponse(User user) {
        Set<String> roleNames = user.getRoles().stream().map(Role::name).collect(Collectors.toSet());
        String token = jwtTokenService.createToken(user.getId(), user.getUsername(), roleNames);
        UserDTO dto = Optional.ofNullable(userService.getUserById(user.getId()))
                .orElseGet(() -> {
                    UserDTO u = new UserDTO();
                    u.setId(user.getId());
                    u.setUsername(user.getUsername());
                    u.setEmail(user.getEmail());
                    u.setRoles(roleNames);
                    return u;
                });
        return new AuthResponse(token, jwtProperties.getExpirationMs(), dto);
    }
}
