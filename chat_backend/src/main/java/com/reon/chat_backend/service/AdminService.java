package com.reon.chat_backend.service;

import com.reon.chat_backend.dto.UserResponseDTO;
import org.springframework.data.domain.Page;

public interface AdminService {
    // All admin related methods
    Page<UserResponseDTO> getUsers(int pageNo, int pageSize);
}
