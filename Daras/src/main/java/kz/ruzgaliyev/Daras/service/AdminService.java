package kz.ruzgaliyev.Daras.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

import kz.ruzgaliyev.Daras.dto.UserResponse;
import kz.ruzgaliyev.Daras.entity.User;
import kz.ruzgaliyev.Daras.mapper.UserMapper;
import kz.ruzgaliyev.Daras.repository.UserRepository;
import kz.ruzgaliyev.Daras.repository.RefreshTokenRepository;
import kz.ruzgaliyev.Daras.entity.RefreshToken;

@Service
public class AdminService {
    
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;


    public AdminService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, AuditLogService auditLogService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.auditLogService = auditLogService;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).stream()
            .map(u -> new UserResponse(u.getId(), u.getEmail(), u.getRole(), u.getCreatedAt(), u.isActive()))
            .toList();
    }


    public void changeUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "cannot find the user with that ID" + id));
        
        if(!role.equals("USER") && !role.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not compatible role");
        }

        user.setRole(role);
        userRepository.save(user);
    }


    public void changeUserStatus(Long id, Boolean state) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find the user with that ID" + id));
        
        user.setActive(state);
        userRepository.save(user);
        
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        
        User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with this ID does not exist!"));
        
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt(), user.isActive());
    }



    public void softDeleteUser(Long id, String adminEmail, HttpServletRequest request) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));

        if(user.getEmail().equals(adminEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin cannot delete themselves");
        }

        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setActive(false);
        userRepository.save(user);

        List<RefreshToken> tokens = refreshTokenRepository.findByUserAndRevokedFalse(user);

        for (RefreshToken t : tokens) {
            t.setRevoked(true);
        }

        refreshTokenRepository.saveAll(tokens);

        
        auditLogService.log(null, user.getEmail(), "USER_SOFT_DELETED", 
        "USER", user.getId(), "Admin deleted the user", 
        request.getRemoteAddr(), request.getHeader("User-Agent"));
    }


    public void restoreUser(Long id, String adminEmail, HttpServletRequest request) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not found"));

        if(!user.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not deleted");
        }

        user.setDeleted(false);
        user.setActive(true);
        user.setDeletedAt(null);
        userRepository.save(user);

        auditLogService.log(null, adminEmail, "USER_RESTORED", 
        "USER", user.getId(), "Admin restored the user", 
        request.getRemoteAddr(), request.getHeader("User-Agent"));

    }

    public void blockUser(Long id, String reason, String adminEmail, HttpServletRequest request) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    if (user.getEmail().equals(adminEmail)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin cannot block themselves");
    }
    user.setActive(false);
    user.setBlockedReason(reason);
    user.setBlockedAt(LocalDateTime.now());
    userRepository.save(user);
    // Revoke all refresh tokens
    List<RefreshToken> tokens = refreshTokenRepository.findByUserAndRevokedFalse(user);
    for (RefreshToken t : tokens) {
        t.setRevoked(true);
    }
    refreshTokenRepository.saveAll(tokens);
    auditLogService.log(null, adminEmail, "USER_BLOCKED", "USER", user.getId(), 
            "Admin blocked user " + user.getEmail() + " for: " + reason, request.getRemoteAddr(), request.getHeader("User-Agent"));
}

    public void unblockUser(Long id, String adminEmail, HttpServletRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setActive(true);
        user.setBlockedReason(null);
        user.setBlockedAt(null);
        userRepository.save(user);
        auditLogService.log(null, adminEmail, "USER_UNBLOCKED", "USER", user.getId(), 
                "Admin unblocked user " + user.getEmail(), request.getRemoteAddr(), request.getHeader("User-Agent"));
    }


}
