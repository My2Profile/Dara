package kz.ruzgaliyev.Daras;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import kz.ruzgaliyev.Daras.dto.UserResponse;
import kz.ruzgaliyev.Daras.entity.User;
import kz.ruzgaliyev.Daras.mapper.UserMapper;
import kz.ruzgaliyev.Daras.repository.RefreshTokenRepository;
import kz.ruzgaliyev.Daras.repository.UserRepository;
import kz.ruzgaliyev.Daras.service.AdminService;
import kz.ruzgaliyev.Daras.service.AuditLogService;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserMapper userMapper;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock AuditLogService auditLogService;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService(userRepository, refreshTokenRepository, auditLogService, userMapper);
    }

    @Test
    void getById_whenMissing_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResponseStatusException.class, () -> adminService.getUserById(1L));
    }

    @Test 
    void getById_whenPresent_returnsUserResponse() {
        User user = new User();
        UserResponse expected = new UserResponse(1L, "Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(expected);

        UserResponse actual = adminService.getUserById(1L);

        assertSame(expected, actual);
        verify(userRepository).findById(1L);
        verify(userMapper).toUserResponse(user);

    }

}
