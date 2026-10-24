package kz.ruzgaliyev.Daras.controller;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kz.ruzgaliyev.Daras.dto.ChangePasswordRequest;
import kz.ruzgaliyev.Daras.dto.UpdateEmailRequest;
import kz.ruzgaliyev.Daras.service.UserService;
import kz.ruzgaliyev.Daras.dto.SessionResponse;
import kz.ruzgaliyev.Daras.dto.UserResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getCurrentUser(currentEmail);
    }

    @PatchMapping("/me/email")
    public String updateEmail(@RequestBody @Valid UpdateEmailRequest request) {
        // Read INSIDE the method, at request time - this is a different value on every
        // call. The constructor above ran once, when the app started; this method body
        // runs once per HTTP request, which is the only place "who is calling right now"
        // actually exists.
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        userService.updateEmail(currentEmail, request.getEmail());

        return "Email updated successfully";
    }

    @PatchMapping("/me/password")
    public String changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        userService.changePassword(currentEmail, request);

        return "Password updated successfully";

    }

    @GetMapping("/me/sessions")
    public List<SessionResponse> getSessions() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserSessions(currentEmail);
    }

    @DeleteMapping("/me/sessions/{sessionId}")
    public String revokeSession(@PathVariable Long sessionId) {

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.revokeUserSession(currentEmail, sessionId);

        return "Session revoked Successfully!";

    }

}
