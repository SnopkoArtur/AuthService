package com.authservice.controller;

import com.authservice.dto.AuthDto;
import com.authservice.dto.JwtDto;
import com.authservice.dto.RegistrationDto;
import com.authservice.service.AuthService;
import com.authservice.service.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * REST controller for authentication functions
 * Provides saving data, login and jwt management
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    /**
     * Saving new user credentials
     *
     * @param dto data for saving
     * @return 200 if saved
     */
    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody RegistrationDto dto) {
        authService.save(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * Authentication with jwt providing
     *
     * @param request data for authentication (login, password)
     * @return JWT structure with access and refresh tokens
     */
    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@RequestBody AuthDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Validating of existing token
     *
     * @param token access token
     * @return 200 if correct
     */
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestParam String token) {
        return ResponseEntity.ok(jwtProvider.validateToken(token));
    }

    /**
     * Update for existing access token using refresh token
     *
     * @param refreshToken current refresh token
     * @return new pair of tokens
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }
}