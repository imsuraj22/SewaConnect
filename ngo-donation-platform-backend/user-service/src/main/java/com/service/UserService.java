package com.service;

import com.dto.UserDTO;
import com.entity.Role;
import com.entity.User;
import com.exceptions.EntityNotFoundException;
import com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserDTO> userKafkaTemplate;
    private final RestTemplate restTemplate;

    @Value("${ngo.service.url}") // e.g. http://localhost:8081
    private String ngoServiceUrl;

    public UserService(UserRepository userRepository,KafkaTemplate<String, UserDTO> userKafkaTemplate,RestTemplate restTemplate){
        this.userRepository=userRepository;
        this.userKafkaTemplate=userKafkaTemplate;
        this.restTemplate=restTemplate;

    }

    public Optional<User> createUser(User user){
        User savedUser = userRepository.save(user);
        System.out.println("User entered ");
        if(savedUser.getRoles().contains(Role.ROLE_NGO)){
            restTemplate.postForObject(
                    ngoServiceUrl + "/api/ngos/register/" + savedUser.getId(),
                    null,
                    Void.class
            );
        }
        return Optional.ofNullable(savedUser);
    }

    //update User

    public Optional<User> updateUser(Long id,User user){
        return userRepository.findById(id)
                .map(existingUser -> {
                   if(user.getEmail()!=null) existingUser.setEmail(user.getEmail());
                   if(user.getPassword()!=null) existingUser.setPassword(user.getPassword());
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
        for(int i=0;i<users.size();i++){
            User u=users.get(i);
            UserDTO newD=new UserDTO();
            newD.setId(u.getId());
            newD.setUsername(u.getUsername());
            newD.setEmail(u.getEmail());
            Set<String> r=new HashSet<>();
            Set<Role> roles=u.getRoles();
            for(Role rr:roles) r.add(rr.toString());
            newD.setRoles(r);
            userDTOS.add(newD);
        }
        return userDTOS;
    }
    public List<UserDTO> getAllUsers(){
        List<User> users= userRepository.findAll();
        List<UserDTO> userDTOS=new ArrayList<>();
        for(int i=0;i<users.size();i++){
            User u=users.get(i);
            UserDTO newD=new UserDTO();
            newD.setId(u.getId());
            newD.setUsername(u.getUsername());
            newD.setEmail(u.getEmail());
            Set<String> r=new HashSet<>();
            Set<Role> roles=u.getRoles();
            for(Role rr:roles) r.add(rr.toString());
            newD.setRoles(r);
            userDTOS.add(newD);
        }
        return userDTOS;
    }

    public UserDTO getUserById(Long id){
        Optional<User> user= userRepository.findById(id);
        if(user.isPresent()){
            User u=user.get();
            UserDTO newD=new UserDTO();
            newD.setId(u.getId());
            newD.setUsername(u.getUsername());
            newD.setEmail(u.getEmail());
            Set<String> r=new HashSet<>();
            Set<Role> roles=u.getRoles();
            for(Role rr:roles) r.add(rr.toString());
            newD.setRoles(r);
            return newD;
        }
        return null;

    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }


}
