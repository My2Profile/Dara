package kz.ruzgaliyev.Daras.controller;

import kz.ruzgaliyev.Daras.dto.*;
import kz.ruzgaliyev.Daras.service.AuthService;
import lombok.NonNull;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@NonNull HttpServletRequest httpRequest, @RequestBody LoginRequest request) {

        return authService.login(httpRequest, request);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@NonNull HttpServletRequest httpRequest, @RequestBody RefreshRequest request) {

        return authService.refresh(httpRequest, request);

    }

    @PostMapping("/logout")
    public String logout(@NonNull HttpServletRequest httpServletRequest, @RequestBody RefreshRequest request) {
        
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        authService.logout(currentEmail, request.getRefreshToken(), httpServletRequest);

        return "Logged out successfully";

    }

    @PostMapping("/logout-all")
    public String logoutAll(@NonNull HttpServletRequest httpServletRequest, @RequestBody RefreshRequest request) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        authService.logoutAll(currentEmail, httpServletRequest);

        return "Logged out from all devices successfully";
    }

}