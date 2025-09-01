package com.service;

import com.entity.Role;
import com.entity.User;
import com.exceptions.EntityNotFoundException;
import com.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;

    }
    public Optional<User> createUser(User user){
        User savedUser = userRepository.save(user);
        return Optional.ofNullable(savedUser);
    }

    public List<User> getUsersByRole(Role role){
        return userRepository.findByRole(role);
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);

    }
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }


}
