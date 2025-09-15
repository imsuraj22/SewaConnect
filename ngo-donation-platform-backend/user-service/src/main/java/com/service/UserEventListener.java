package com.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventListener {

    private final UserService userService;
    public UserEventListener(UserService userService){
        this.userService=userService;
    }

    @KafkaListener(topics = "user-delete-approved", groupId = "user-group")
    public void handleUserDeleteApproval(Long userId) {
        Long id = Long.valueOf(userId);
        userService.deleteById(id);
        //send user mail
    }

    // Handle admin rejection
    @KafkaListener(topics = "user-delete-rejected", groupId = "user-group")
    public void handleUserDeleteRejection(Long userId) {
        System.out.println("User deletion rejected for id: " + userId);
        //send user a mail
    }
}
