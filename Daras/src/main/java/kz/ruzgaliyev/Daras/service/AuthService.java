package kz.ruzgaliyev.Daras.service;

import kz.ruzgaliyev.Daras.config.JwtProperties;
import kz.ruzgaliyev.Daras.dto.*;
import kz.ruzgaliyev.Daras.entity.RefreshToken;
import kz.ruzgaliyev.Daras.entity.User;
import kz.ruzgaliyev.Daras.repository.RefreshTokenRepository;
import kz.ruzgaliyev.Daras.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuditLogService auditLogService;

    public AuthService(
        UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
        JwtProperties jwtProperties, RefreshTokenRepository refreshTokenRepository,
        AuditLogService auditLogService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.refreshTokenRepository = refreshTokenRepository;
        this.auditLogService = auditLogService;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exist");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setActive(true);
        userRepository.save(user);

        auditLogService.log(
        user.getId(),
        user.getEmail(),
        "USER_REGISTERED",
        "USER",
        user.getId(),
        "User registered successfully",
        null,
        null
        );

        return "User registered successfully";
    }

    public LoginResponse login(HttpServletRequest httprequest, LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials"));

        if(user.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User account is deleted");
        }

        if(!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is blocked!");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credentials");
        }


        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole());

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);

        refreshToken.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtProperties.refresh().expiration())));
        
        refreshToken.setRevoked(false);
        refreshToken.setUserAgent(httprequest.getHeader("User-Agent"));
        refreshToken.setIpAddress(httprequest.getRemoteAddr());

        refreshTokenRepository.save(refreshToken);


        auditLogService.log(
        user.getId(),
        user.getEmail(),
        "USER_LOGIN",
        "USER",
        user.getId(),
        "User logged in successfully",
        httprequest.getRemoteAddr(),
        httprequest.getHeader("User-Agent")
        );
        


        return new LoginResponse(accessToken, refreshToken.getToken(), "Bearer", jwtProperties.access().expiration());


    }

    public LoginResponse refresh(HttpServletRequest httpServletRequest, RefreshRequest request) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if(refreshToken.getRevoked()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The token is revoked!");
        }

        if(LocalDateTime.now().isAfter(refreshToken.getExpiresAt()) || !refreshToken.getUser().isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        RefreshToken newRefreshToken = new RefreshToken();

        newRefreshToken.setUser(refreshToken.getUser());
        newRefreshToken.setToken(UUID.randomUUID().toString());
        newRefreshToken.setExpiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtProperties.refresh().expiration())));
        newRefreshToken.setRevoked(false);
        newRefreshToken.setUserAgent(httpServletRequest.getHeader("User-Agent"));
        newRefreshToken.setIpAddress(httpServletRequest.getRemoteAddr());

        refreshTokenRepository.save(newRefreshToken);

        String accessToken = jwtService.generateToken(
            refreshToken.getUser().getEmail(),
            refreshToken.getUser().getRole()
        );

        return new LoginResponse(accessToken, newRefreshToken.getToken(), "Bearer", jwtProperties.access().expiration());

    }

    public void logout(String email, String refreshToken, HttpServletRequest request) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "cannot find the token"));

        if(!token.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot revoke someone else's token");
        }

        token.setRevoked(true);
        refreshTokenRepository.save(token);

        auditLogService.log(token.getUser().getId(), email, "USER_LOGOUT", 
        "USER", token.getUser().getId(), "User logged out", 
        request.getRemoteAddr(), request.getHeader("User-Agent"));

    }

    public void logoutAll(String email, HttpServletRequest request) {

        User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        List<RefreshToken> activeSessions = refreshTokenRepository.findByUserAndRevokedFalse(user);

        for (RefreshToken refreshToken : activeSessions) {
            refreshToken.setRevoked(true);
        }

        refreshTokenRepository.saveAll(activeSessions);

        auditLogService.log(user.getId(), email, "USER_LOGOUT_ALL", 
        "USER", user.getId(), "User logged out from all the devices",
        request.getRemoteAddr(), request.getHeader("User-Agent"));

    }

}