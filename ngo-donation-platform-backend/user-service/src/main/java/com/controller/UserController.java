package com.controller;

import com.entity.Role;
import com.entity.User;
import com.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")

public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return userService.createUser(user)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Failed to create user"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,@RequestBody User user){
        return userService.updateUser(id,user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
