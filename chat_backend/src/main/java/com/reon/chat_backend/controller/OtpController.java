package com.reon.chat_backend.controller;

import com.reon.chat_backend.dto.AccountVerificationDTO;
import com.reon.chat_backend.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OtpController {
    private final Logger log = LoggerFactory.getLogger(OtpController.class);
    private final UserService userService;

    public OtpController(UserService userService) {
        this.userService = userService;
    }

    // Account Verification -> Registration
    @PostMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(@RequestParam String email,
                                                @Valid @RequestBody AccountVerificationDTO accountVerificationDTO) {
        try {
            log.info("Controller:: Verifying account {}", accountVerificationDTO);
            userService.verifyAccount(email, accountVerificationDTO.getOtp());
            log.info("Controller:: Verified account {}", accountVerificationDTO);
            return ResponseEntity.ok().body("Account verified");
        } catch (Exception e) {
            log.error("Controller:: Error verifying account {}", accountVerificationDTO, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Resend Account Verification OTP -> Registration
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendVerificationOTP(@RequestParam String email){
        try {
            log.info("Controller:: Resending otp {}", email);
            userService.sendVerificationEmail(email);
            log.info("Controller:: OTP sent {}", email);
            return ResponseEntity.ok().body("OTP sent");
        } catch (Exception e) {
            log.error("Controller:: Error resending otp {}", email, e);
            throw new RuntimeException(e);
        }
    }
}
