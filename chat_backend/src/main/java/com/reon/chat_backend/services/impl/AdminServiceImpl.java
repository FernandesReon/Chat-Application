package com.reon.chat_backend.services.impl;

import com.reon.chat_backend.dtos.UserResponseDTO;
import com.reon.chat_backend.mapper.UserMapper;
import com.reon.chat_backend.models.User;
import com.reon.chat_backend.repositories.UserRepository;
import com.reon.chat_backend.services.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class AdminServiceImpl implements AdminService {
    private final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);
    private final UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<UserResponseDTO> getUsers(int pageNo, int pageSize) throws AccessDeniedException {
        String authenticatedEmail = verifyAdminAccess();

        log.info("Service:: Authenticated email: {}", authenticatedEmail);
        log.info("Service:: Fetching all users with pagination: pageNo={}, pageSize={}", pageNo, pageSize);
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserMapper::responseToUser);
    }

    @Override
    public String verifyAdminAccess() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()){
            log.warn("Service:: No authenticated user found");
            throw new AccessDeniedException("User must be authenticated to perform this action");
        }

        String authenticatedEmail = authentication.getName();

        userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("User not found with email: " + authenticatedEmail);
                });

        if (!isAdmin(authentication)){
            log.warn("Service:: User {} attempted to perform and admin action but is not an admin", authenticatedEmail);
            throw new AccessDeniedException("Only admins can perform this action");
        }
        return authenticatedEmail;
    }

    @Override
    public boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
