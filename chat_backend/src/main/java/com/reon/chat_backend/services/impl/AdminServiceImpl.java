package com.reon.chat_backend.services.impl;

import com.reon.chat_backend.dtos.user.UserResponseDTO;
import com.reon.chat_backend.mapper.UserMapper;
import com.reon.chat_backend.models.User;
import com.reon.chat_backend.repositories.UserRepository;
import com.reon.chat_backend.services.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;


@Service
public class AdminServiceImpl implements AdminService {
    private final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);
    private final UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<UserResponseDTO> getUsers(int pageNo, int pageSize)  {
        log.info("Service:: Fetching all users with pagination: pageNo={}, pageSize={}", pageNo, pageSize);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserMapper::responseToUser);
    }
}
