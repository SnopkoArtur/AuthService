package com.authservice;

import com.authservice.dao.UserCredentialsRepository;
import com.authservice.dto.AuthDto;
import com.authservice.dto.JwtDto;
import com.authservice.dto.RegistrationDto;
import com.authservice.entity.Role;
import com.authservice.entity.UserCredentials;
import com.authservice.exception.AuthException;
import com.authservice.service.AuthServiceImpl;
import com.authservice.service.JwtProviderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserCredentialsRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProviderImpl jwtProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserCredentials userCredentials;

    @BeforeEach
    void setUp() {
        userCredentials = new UserCredentials();
        userCredentials.setLogin("test_user");
        userCredentials.setPassword("hashed_password");
        userCredentials.setRole(Role.USER);
        userCredentials.setUserId(1L);
    }

    @Test
    void save_ShouldEncodePasswordAndSave() {
        RegistrationDto request = new RegistrationDto("test_user", "raw_password", Role.USER, 1L);
        when(passwordEncoder.encode("raw_password")).thenReturn("hashed_password");
        authService.save(request);
        verify(passwordEncoder).encode("raw_password");
        verify(repository).save(any(UserCredentials.class));
    }

    @Test
    void login_Success() {
        AuthDto request = new AuthDto("test_user", "raw_password");
        when(repository.findByLogin("test_user")).thenReturn(Optional.of(userCredentials));
        when(passwordEncoder.matches("raw_password", "hashed_password")).thenReturn(true);
        when(jwtProvider.generateAccessToken(userCredentials)).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(userCredentials)).thenReturn("refresh-token");

        JwtDto response = authService.login(request);
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void login_InvalidPassword_ShouldThrowException() {
        AuthDto request = new AuthDto("test_user", "wrong_password");
        when(repository.findByLogin("test_user")).thenReturn(Optional.of(userCredentials));
        when(passwordEncoder.matches("wrong_password", "hashed_password")).thenReturn(false);

        assertThrows(AuthException.class, () -> authService.login(request));
        verify(jwtProvider, never()).generateAccessToken(any());
    }

    @Test
    void login_UserNotFound_ShouldThrowException() {
        AuthDto request = new AuthDto("unknown", "password");
        when(repository.findByLogin("unknown")).thenReturn(Optional.empty());
        assertThrows(AuthException.class, () -> authService.login(request));
    }

    @Test
    void refresh_Success() {
        String oldRefreshToken = "old-refresh-token";
        when(jwtProvider.validateToken(oldRefreshToken)).thenReturn(true);
        when(jwtProvider.getLoginFromToken(oldRefreshToken)).thenReturn("test_user");
        when(repository.findByLogin("test_user")).thenReturn(Optional.of(userCredentials));
        when(jwtProvider.generateAccessToken(userCredentials)).thenReturn("new-access-token");
        when(jwtProvider.generateRefreshToken(userCredentials)).thenReturn("new-refresh-token");

        JwtDto response = authService.refresh(oldRefreshToken);

        assertEquals("new-access-token", response.getAccessToken());
        verify(repository).findByLogin("test_user");
    }

    @Test
    void refresh_InvalidToken_ShouldThrowException() {
        String invalidToken = "invalid-token";
        when(jwtProvider.validateToken(invalidToken)).thenReturn(false);

        assertThrows(AuthException.class, () -> authService.refresh(invalidToken));
    }
}