package kz.ruzgaliyev.Daras;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hibernate.validator.constraints.Mod10Check;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import kz.ruzgaliyev.Daras.repository.UserRepository;

import kz.ruzgaliyev.Daras.service.AuditLogService;

import kz.ruzgaliyev.Daras.repository.RefreshTokenRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private final UserRepository userRepository;
    @Mock
    private final RefreshTokenRepository refreshTokenRepository;
    @Mock
    private AuditLogService auditLogService;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        auditLogService = mock(AuditLogService.class);
    }

    @Test
    void getAdminById_whenUnknownId_thenThrowsResponseStatusException() {
        Long unknownId = 999L;
        when(adminRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            adminService.getAdminById(unknownId);
        });
    }
}
