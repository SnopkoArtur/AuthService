package com.authservice;

import com.authservice.entity.Role;
import com.authservice.entity.UserCredentials;
import com.authservice.service.JwtProvider;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;
    private final String testSecret = "testkeytestkeytestkeytestkeytestkey";

    private UserCredentials testUser;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "secret", testSecret);
        jwtProvider.init();
        testUser = new UserCredentials();
        testUser.setLogin("test_login");
        testUser.setUserId(123L);
        testUser.setRole(Role.USER);
    }

    @Test
    void generateAccessToken_Success() {
        String token = jwtProvider.generateAccessToken(testUser);
        assertNotNull(token);
        assertFalse(token.isEmpty());
        String login = jwtProvider.getLoginFromToken(token);
        assertEquals("test_login", login);
    }

    @Test
    void generateRefreshToken_Success() {
        String token = jwtProvider.generateRefreshToken(testUser);
        assertNotNull(token);
        assertTrue(jwtProvider.validateToken(token));
    }

    @Test
    void validateToken_Valid() {
        String token = jwtProvider.generateAccessToken(testUser);
        boolean isValid = jwtProvider.validateToken(token);
        assertTrue(isValid);
    }

    @Test
    void validateToken_Invalid() {
        String brokenToken = "eyJhbGciOiJIUzI1NiJ9.broken.token";
        boolean isValid = jwtProvider.validateToken(brokenToken);
        assertFalse(isValid);
    }

    @Test
    void validateToken_WrongSignature() {
        String fakeToken = Jwts.builder()
                .setSubject("fake")
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor("newtestkeynewtestkeynewtestkeynewtestkey".getBytes()))
                .compact();
        boolean isValid = jwtProvider.validateToken(fakeToken);
        assertFalse(isValid);
    }

    @Test
    void getLoginFromToken_Success() {
        String token = jwtProvider.generateAccessToken(testUser);
        String login = jwtProvider.getLoginFromToken(token);
        assertEquals("test_login", login);
    }
}