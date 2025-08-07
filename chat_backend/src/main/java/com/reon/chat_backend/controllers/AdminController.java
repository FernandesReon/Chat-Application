package com.reon.chat_backend.controllers;

import com.reon.chat_backend.dtos.UserResponseDTO;
import com.reon.chat_backend.services.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> fetchUsers(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        try {
            log.info("Controller:: Fetching all users from pageNo={} pageSize={}", page, size);
            Page<UserResponseDTO> allUsers = adminService.getUsers(page, size);
            return ResponseEntity.ok().body(allUsers);
        } catch (Exception e) {
            log.error("Controller:: Fetching all users failed", e);
            throw new RuntimeException(e);
        }
    }
}
