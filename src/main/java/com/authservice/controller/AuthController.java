package com.authservice.controller;

import com.authservice.dto.AuthDto;
import com.authservice.dto.JwtDto;
import com.authservice.dto.RegistrationDto;
import com.authservice.service.AuthService;
import com.authservice.service.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody RegistrationDto dto) {
        authService.save(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@RequestBody AuthDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestParam String token) {
        return ResponseEntity.ok(jwtProvider.validateToken(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }
}