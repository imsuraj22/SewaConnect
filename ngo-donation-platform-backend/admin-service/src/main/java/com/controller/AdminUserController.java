package com.controller;

import com.dto.NGODto;
import com.dto.UserDTO;
import com.service.AdminUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {
    private final AdminUserService adminUserService;
    public AdminUserController(AdminUserService adminUserService){
        this.adminUserService=adminUserService;
    }

    @GetMapping("/by-role/{role}")
    public List<UserDTO> getUserByRole(String role) {
        return adminUserService.getUserByRole(role);
    }
    @GetMapping("/{id}")
    public UserDTO getUserById(Long id) {
        return adminUserService.getUserById(id);
    }

    @GetMapping("/pending-approve")
    public List<UserDTO> getPendingUsers(){
        return adminUserService.getUsersToDelete();
    }

    @PostMapping("/approved/{id}")
    public void approvedDelete(@PathVariable Long id){
        adminUserService.approvedUser(id);
    }

    @PostMapping("/rejected/{id}")
    public void rejectedDelete(@PathVariable Long id){
        adminUserService.rejectedUser(id);
    }



}
