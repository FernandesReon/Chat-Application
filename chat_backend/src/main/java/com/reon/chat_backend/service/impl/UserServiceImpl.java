package com.reon.chat_backend.service.impl;

import com.reon.chat_backend.dto.UserRequestDTO;
import com.reon.chat_backend.dto.UserResponseDTO;
import com.reon.chat_backend.exception.EmailAlreadyExistsException;
import com.reon.chat_backend.exception.InvalidOTPException;
import com.reon.chat_backend.exception.OTPExpiredException;
import com.reon.chat_backend.exception.UserNotFoundException;
import com.reon.chat_backend.mapper.UserMapper;
import com.reon.chat_backend.model.User;
import com.reon.chat_backend.model.VerificationToken;
import com.reon.chat_backend.repository.UserRepository;
import com.reon.chat_backend.service.EmailService;
import com.reon.chat_backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class UserServiceImpl implements UserService {
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.error("Email already exists");
            throw new EmailAlreadyExistsException("User already exists");
        }

        log.info("Service:: Creating new user");
        User user = UserMapper.toEntity(userRequestDTO);

        // TODO:: encode the password once spring security is configured.

        User savedUser = userRepository.save(user);
        log.info("Service:: Saving user {}", user);

        try {
            log.info("Service:: Sending verification email");
            sendVerificationEmail(userRequestDTO.getEmail());
            log.info("Service:: Verification email sent");
        } catch (Exception e) {
            log.error("Service:: Error sending verification email");
            throw new RuntimeException(e);
        }

        return UserMapper.responseToUser(savedUser);
    }


    // Generate 6-digits OTP
    public static String generateOTP(){
        SecureRandom random = new SecureRandom();
        int otp = random.nextInt(900000) + 100000;
        return String.valueOf(otp);
    }

    @Override
    public void sendVerificationEmail(String email) {
        // Check if user exist
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        VerificationToken verificationToken = new VerificationToken();

        // Assign an OTP
        String otp = generateOTP();
        verificationToken.setToken(otp);

        // Set expiry time
        long otpExpiryTime = System.currentTimeMillis() +  (15 * 60 * 1000);
        verificationToken.setExpiryDate(otpExpiryTime);

        verificationToken.setUser(user);

        // save otp to database
        user.setToken(verificationToken);
        userRepository.save(user);

        try {
            emailService.verificationOTP(email, user.getName(), otp);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void verifyAccount(String email, String verificationCode) {
        // Check if user exist
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        VerificationToken verificationToken = user.getToken();

        if (verificationToken == null || verificationToken.getToken() == null) {
            throw new InvalidOTPException("No OTP found or it was already used.");
        }

        if (!verificationToken.getToken().equals(verificationCode)){
            throw new InvalidOTPException("OTP does not match");
        }

        if (verificationToken.getExpiryDate() < System.currentTimeMillis()){
            throw new OTPExpiredException("OTP has expired");
        }

        user.setEmailVerified(true);
        user.setAccountEnabled(true);

        verificationToken.setToken(null);
        verificationToken.setExpiryDate(0L);

        userRepository.save(user);

        try {
            log.info("Service:: Sending welcome email to user {}", user);
            emailService.sendWelcomeEmail(email, user.getName());
            log.info("Service:: Welcome email sent");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
