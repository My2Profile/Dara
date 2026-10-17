package kz.ruzgaliyev.Daras.repository;

import kz.ruzgaliyev.Daras.entity.AuditLog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog>{
    @Query("SELECT a FROM AuditLog a WHERE a.actorUserId = :actorUserId AND a.action = :action AND a.targetType = :targetType AND a.targetId = :targetId")
    List<AuditLog> findByActorUserIdAndActionAndTargetTypeAndTargetId(Long actorUserId, String action, String targetType, Long targetId);

} 
