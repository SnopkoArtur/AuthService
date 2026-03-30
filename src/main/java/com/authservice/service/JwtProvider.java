package com.authservice.service;

import com.authservice.entity.UserCredentials;

/**
 * Interface for working with JWT
 * Creates, validates jwt tokens, extracts data from jwt
 */
public interface JwtProvider {

    /**
     * Generates access token
     * Includes id and user role inside
     *
     * @param user user credentials
     * @return access jwt token
     */
    String generateAccessToken(UserCredentials user);

    /**
     * Generates refresh token
     *
     * @param user user credentials
     * @return refresh jwt token
     */
    String generateRefreshToken(UserCredentials user);

    /**
     * Validates jwt token
     *
     * @param token string jwt token
     * @return true if valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Gets login from token
     *
     * @param token string jwt token
     * @return login of user
     */
    String getLoginFromToken(String token);
}