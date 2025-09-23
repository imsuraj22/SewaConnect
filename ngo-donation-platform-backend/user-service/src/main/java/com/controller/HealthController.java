package com.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-health")
public class HealthController {

    @GetMapping
    public String health(){
        return "User service is working fine";
    }
}
