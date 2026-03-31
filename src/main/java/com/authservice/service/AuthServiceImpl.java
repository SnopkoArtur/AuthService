package com.authservice.service;

import com.authservice.dao.UserCredentialsRepository;
import com.authservice.dto.AuthDto;
import com.authservice.dto.JwtDto;
import com.authservice.dto.RegistrationDto;
import com.authservice.entity.UserCredentials;
import com.authservice.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialsRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public void save(RegistrationDto dto) {
        UserCredentials uc = new UserCredentials();
        uc.setLogin(dto.getLogin());
        uc.setPassword(passwordEncoder.encode(dto.getPassword()));
        uc.setRole(dto.getRole());
        uc.setUserId(dto.getUserId());
        repository.save(uc);
    }

    @Override
    public JwtDto login(AuthDto request){
        UserCredentials user = repository.findByLogin(request.getLogin())
                .orElseThrow(() -> new AuthException("Invalid login or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid login or password");
        }

        return new JwtDto(
                jwtProvider.generateAccessToken(user),
                jwtProvider.generateRefreshToken(user)
        );
    }

    @Override
    public JwtDto refresh(String refreshToken)  {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new AuthException("Invalid refresh token");
        }
        String login = jwtProvider.getLoginFromToken(refreshToken);
        UserCredentials user = repository.findByLogin(login)
                .orElseThrow(() -> new AuthException("User not found"));

        return new JwtDto(
                jwtProvider.generateAccessToken(user),
                jwtProvider.generateRefreshToken(user)
        );
    }
}