package com.reon.chat_backend.services;

import com.reon.chat_backend.dtos.UserLoginDTO;
import com.reon.chat_backend.dtos.UserProfileDTO;
import com.reon.chat_backend.dtos.UserRequestDTO;
import com.reon.chat_backend.dtos.UserResponseDTO;
import com.reon.chat_backend.jwt.JwtResponse;

public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);
    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);
    void deleteUser(Long id);

    // Account Verification
    void sendVerificationEmail(String email);
    void verifyAccount(String email, String verificationCode);

    // reset password
    void sendResetPasswordOTP(String email);
    void verifyResetPasswordOTP(String email, String verificationCode);
    void resetPassword(String email, String verificationCode, String newPassword);

    // login
    JwtResponse authenticateUser(UserLoginDTO loginDTO);
    boolean isUserLoggedIn(Long id);

    // profile
    UserProfileDTO userProfile();
}
