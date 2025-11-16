package com.inventory.service;

import com.inventory.entity.AuditLog;
import com.inventory.entity.User;
import com.inventory.repository.AuditLogRepository;
import com.inventory.repository.UserRepository;
import com.inventory.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {
    
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public void log(String action, String entityType, Long entityId, String description) {
        logWithChanges(action, entityType, entityId, description, null, null);
    }
    
    @Transactional
    public void logWithChanges(String action, String entityType, Long entityId, 
                               String description, String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
                User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
                log.setUser(user);
            }
        } catch (Exception e) {
            // If we can't get user, continue without it
        }
        
        auditLogRepository.save(log);
    }
    
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllOrderByTimestampDesc();
    }
    
    public List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByDateRange(start, end);
    }
    
    public List<AuditLog> getLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
}
