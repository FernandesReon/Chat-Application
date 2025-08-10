package com.reon.chat_backend.services;

import com.reon.chat_backend.dtos.user.UserResponseDTO;
import org.springframework.data.domain.Page;

public interface AdminService {
    // All admin related methods
    Page<UserResponseDTO> getUsers(int pageNo, int pageSize);
}
