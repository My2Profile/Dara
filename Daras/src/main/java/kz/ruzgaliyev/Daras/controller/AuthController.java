package kz.ruzgaliyev.Daras.controller;

import kz.ruzgaliyev.Daras.dto.*;
import kz.ruzgaliyev.Daras.service.AuthService;
import lombok.NonNull;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Authentication", description = "Endpoints for user authentication and authorization")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User registration", description = "Register a new user and return a confirmation message")
    @PostMapping("/register")
    public String register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary = "User login", description = "Authenticate user and return access and refresh tokens")
    @PostMapping("/login")
    public LoginResponse login(@NonNull HttpServletRequest httpRequest, @RequestBody @Valid LoginRequest request) {

        return authService.login(httpRequest, request);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@NonNull HttpServletRequest httpRequest, @RequestBody @Valid RefreshRequest request) {

        return authService.refresh(httpRequest, request);

    }

    @PostMapping("/logout")
    public String logout(@NonNull HttpServletRequest httpServletRequest, @RequestBody @Valid RefreshRequest request) {
        
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        authService.logout(currentEmail, request.getRefreshToken(), httpServletRequest);

        return "Logged out successfully";

    }

    @PostMapping("/logout-all")
    public String logoutAll(@NonNull HttpServletRequest httpServletRequest, @RequestBody @Valid RefreshRequest request) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        authService.logoutAll(currentEmail, httpServletRequest);

        return "Logged out from all devices successfully";
    }

}