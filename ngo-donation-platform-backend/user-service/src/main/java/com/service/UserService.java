package com.service;

import com.dto.UserDTO;
import com.entity.Role;
import com.entity.User;
import com.exceptions.EntityNotFoundException;
import com.repository.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserDTO> userKafkaTemplate;
    private final PasswordEncoder passwordEncoder;
    private final NgoServiceClient ngoServiceClient;

    public UserService(
            UserRepository userRepository,
            KafkaTemplate<String, UserDTO> userKafkaTemplate,
            PasswordEncoder passwordEncoder,
            NgoServiceClient ngoServiceClient
    ) {
        this.userRepository = userRepository;
        this.userKafkaTemplate = userKafkaTemplate;
        this.passwordEncoder = passwordEncoder;
        this.ngoServiceClient = ngoServiceClient;
    }

    public Optional<User> createUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        User savedUser = userRepository.save(user);
        System.out.println("User entered ");
        if (savedUser.getRoles().contains(Role.ROLE_NGO)) {
            ngoServiceClient.registerNgoStub(savedUser.getId());
        }
        return Optional.ofNullable(savedUser);
    }

    //update User

    public Optional<User> updateUser(Long id,User user){
        return userRepository.findById(id)
                .map(existingUser -> {
                   if(user.getEmail()!=null) existingUser.setEmail(user.getEmail());
                   if (user.getPassword() != null && !user.getPassword().isBlank()) {
                       existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                   }
                   if(user.getUsername()!=null) existingUser.setUsername(user.getUsername());
                   if(user.getRoles()!=null) existingUser.setRoles(user.getRoles());
                    return userRepository.save(existingUser);
                });
    }

    public void deleteUser(Long id){
        Optional<User> user=userRepository.findById(id);
        if(user.isPresent()){
            User u=user.get();

            UserDTO userDTO=new UserDTO();
            userDTO.setId(u.getId());
            userDTO.setUsername(u.getUsername());
            userDTO.setEmail(u.getEmail());
            Set<String> r=new HashSet<>();
            Set<Role> roles=u.getRoles();
            for(Role rr:roles) r.add(rr.toString());
            userDTO.setRoles(r);
          userKafkaTemplate.send("user-delete-request",userDTO);
          System.out.println("Kafka is running in User service");
        }

    }
    public void deleteById(Long id){
        userRepository.deleteById(id);
    }

    //admin only methods
    public List<UserDTO> getUsersByRole(Role role){
        List<User> users= userRepository.findByRoles(role);
        List<UserDTO> userDTOS=new ArrayList<>();
        for (User u : users) {
            userDTOS.add(toUserDto(u));
        }
        return userDTOS;
    }
    public List<UserDTO> getAllUsers(){
        List<User> users= userRepository.findAll();
        List<UserDTO> userDTOS=new ArrayList<>();
        for (User u : users) {
            userDTOS.add(toUserDto(u));
        }
        return userDTOS;
    }

    public UserDTO getUserById(Long id){
        return userRepository.findById(id).map(this::toUserDto).orElse(null);
    }

    private UserDTO toUserDto(User u) {
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        Set<String> roleNames = new HashSet<>();
        for (Role role : u.getRoles()) {
            roleNames.add(role.name());
        }
        dto.setRoles(roleNames);
        dto.setActive(u.isActive());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setUpdatedAt(u.getUpdatedAt());
        return dto;
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }


}
