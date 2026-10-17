package kz.ruzgaliyev.Daras.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name = "audit_logs")
@Getter 
@Setter 
@NoArgsConstructor 
public class AuditLog {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long actorUserId;

    private String actorEmail;

    @Column(nullable=false)
    private String action;

    private String targetType;

    private Long targetId;

    @Column(length = 1000)
    private String description;

    private String ipAddress;

    private String userAgent;

    @CreationTimestamp 
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AuditLog(Long actorUserId, String actorEmail, String action, String targetType, 
        Long targetId, String description, String ipAddress, String userAgent) {

        this.actorUserId = actorUserId;
        this.actorEmail = actorEmail;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.description = description;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;

    }
    
    
}
