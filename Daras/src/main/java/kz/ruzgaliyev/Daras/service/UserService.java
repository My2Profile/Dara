package kz.ruzgaliyev.Daras.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import kz.ruzgaliyev.Daras.dto.ChangePasswordRequest;
import kz.ruzgaliyev.Daras.entity.RefreshToken;
import kz.ruzgaliyev.Daras.entity.User;
import kz.ruzgaliyev.Daras.repository.UserRepository;
import kz.ruzgaliyev.Daras.dto.UserResponse;
import kz.ruzgaliyev.Daras.repository.RefreshTokenRepository;
import kz.ruzgaliyev.Daras.dto.SessionResponse;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void updateEmail(String currentEmail, String newEmail) {
        // currentEmail comes from the controller, which read it out of the verified
        // token via SecurityContextHolder - this is the row we are allowed to change.
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // existsByEmail returns a plain boolean, not an Optional - this is the
        // correct way to check "is this value already taken", unlike the
        // `findByEmail(...) == null` check from before, which could never be true.
        if (userRepository.existsByEmail(newEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        user.setEmail(newEmail);
        userRepository.save(user);
    }

    public void changePassword(String currentEmail, ChangePasswordRequest request) {

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with this email not found"));
        
        
        if(passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
        }
        else { 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your old password does not match. If forgot then reset password");
        }

    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with this email not found"));
        
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt(), user.isActive());
    }

    public List<SessionResponse> getUserSessions(String email) {
        User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with this email is not found"));
        
        return refreshTokenRepository.findByUserAndRevokedFalse(user).stream()
                .map(t -> new SessionResponse(
                    t.getId(),
                    t.getCreatedAt(),
                    t.getExpiresAt(),
                    t.getUserAgent(),
                    t.getIpAddress(),
                    t.getRevoked()
                )).toList();
        
    }


    public void revokeUserSession(String email, Long sessionId) {

        RefreshToken token = refreshTokenRepository.findById(sessionId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        
        if(!token.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot revoke another user's session");
        }

        token.setRevoked(true);
        refreshTokenRepository.save(token);

    }

}
