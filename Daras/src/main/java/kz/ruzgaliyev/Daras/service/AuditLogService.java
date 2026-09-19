package kz.ruzgaliyev.Daras.service;

import kz.ruzgaliyev.Daras.entity.AuditLog;
import org.springframework.stereotype.Service;

import kz.ruzgaliyev.Daras.repository.AuditLogRepository;

@Service 
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(
            Long actorUserId,
            String actorEmail,
            String action,
            String targetType,
            Long targetId,
            String description,
            String ipAddress,
            String userAgent
    ) {
        AuditLog auditLog = new AuditLog(
                actorUserId,
                actorEmail,
                action,
                targetType,
                targetId,
                description,
                ipAddress,
                userAgent
        );
        auditLogRepository.save(auditLog);
    }
    
}
