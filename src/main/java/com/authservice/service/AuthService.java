package com.authservice.service;

import com.authservice.dto.AuthDto;
import com.authservice.dto.JwtDto;
import com.authservice.dto.RegistrationDto;

/**
 * Service for authentication
 * Provides registration, creation/update of jwt tokens
 */
public interface AuthService {

    /**
     * Saves credentials to database
     *
     * @param dto object with log,password, role and id
     */
    void save(RegistrationDto dto);

    /**
     * Maketh authentication to the system
     *
     * @param request object with login and password
     * @return access and refresh tokens
     * @throws com.authservice.exception.AuthException if data is wrong
     */
    JwtDto login(AuthDto request);

    /**
     * Refresh both access and refresh tokens using refresh token
     *
     * @param refreshToken current refresh token
     * @return new pair of access and refresh tokens
     * @throws com.authservice.exception.AuthException if token is invalid or data is invalid
     */
    JwtDto refresh(String refreshToken);
}