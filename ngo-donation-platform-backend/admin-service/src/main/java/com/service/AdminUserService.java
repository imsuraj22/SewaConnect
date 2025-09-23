package com.service;

import com.dto.NGODto;
import com.dto.UserDTO;
import com.entity.UserData;
import com.repository.NGODataRepo;
import com.repository.NGODocumentDataRepo;
import com.repository.UserDataRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AdminUserService {

    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, UserDTO> userKafkaTemplate;
    private final KafkaTemplate<String, Long> longKafkaTemplate;
    private final UserDataRepo userDataRepo;




    public AdminUserService(RestTemplate restTemplate, KafkaTemplate<String, UserDTO> userKafkaTemplate,
                            KafkaTemplate<String,String> kafkaTemplate,
                            KafkaTemplate<String, Long> longKafkaTemplate,UserDataRepo userDataRepo) {
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.userKafkaTemplate=userKafkaTemplate;
        this.longKafkaTemplate=longKafkaTemplate;
        this.userDataRepo=userDataRepo;


    }
    @Value("${user.service.url}") // e.g. http://localhost:8081
    private String userServiceUrl;


    public List<UserDTO> getUserByRole(String role){
        UserDTO[] res=restTemplate.getForObject(
                userServiceUrl+"/internal/users"+"/by-role/"+role,
                UserDTO[].class
        );
        return Arrays.asList(res);
    }
    public UserDTO getUserById(Long id){
        return restTemplate.getForObject(
                userServiceUrl+"/internal/users/"+id,
                UserDTO.class
        );
        //return res;
    }



    @KafkaListener(topics = "user-delete-request", groupId = "donation-group")
    public void processUserDeleteRequest(UserDTO userDTO) {

        UserData user=new UserData();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setRoles(userDTO.getRoles());
        userDataRepo.save(user);
        System.out.println("Kafka is running in Admin service");


    }

    public List<UserDTO> getUsersToDelete(){
        List<UserData> users=userDataRepo.findAll();
        List<UserDTO> toDelete=new ArrayList<>();
        for(int i=0;i<users.size();i++){
            UserData u=users.get(i);
            UserDTO dto=new UserDTO();
            dto.setId(u.getId());
            dto.setEmail(u.getEmail());
            dto.setUsername(u.getUsername());
            dto.setRoles(u.getRoles());
            toDelete.add(dto);
        }
        return toDelete;
    }
    public void approvedUser(Long id){
        Optional<UserData> userData=userDataRepo.findById(id);
        if(userData.isPresent()){
            UserData u=userData.get();
            longKafkaTemplate.send("user-delete-approved", u.getId());
            userDataRepo.deleteById(id);
        }

    }
    public void rejectedUser(Long id){
        Optional<UserData> userData=userDataRepo.findById(id);
        if(userData.isPresent()){
            UserData u=userData.get();
            longKafkaTemplate.send("user-delete-rejected", u.getId());
            userDataRepo.deleteById(id);
        }

    }
}
