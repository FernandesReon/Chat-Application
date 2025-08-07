package com.reon.chat_backend.services;

import com.reon.chat_backend.dtos.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.nio.file.AccessDeniedException;

public interface AdminService {
    // All admin related methods
    Page<UserResponseDTO> getUsers(int pageNo, int pageSize) throws AccessDeniedException;

    // check if admin
    String verifyAdminAccess() throws AccessDeniedException;
    boolean isAdmin(Authentication auth);
}
