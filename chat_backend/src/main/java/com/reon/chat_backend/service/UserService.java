package com.reon.chat_backend.service;

import com.reon.chat_backend.dto.UserRequestDTO;
import com.reon.chat_backend.dto.UserResponseDTO;

public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);

    // Account Verification
    void sendVerificationEmail(String email);
    void verifyAccount(String email, String verificationCode);
}
