package com.example.controller;

import com.example.service.UserValidationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Email Verification")
@RestController
@RequiredArgsConstructor
public class EmailVerificationController {

    private final UserValidationService userValidationService;

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        userValidationService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }
}