package com.inventory.controller;

import com.inventory.entity.AuditLog;
import com.inventory.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class AuditController {
    
    private final AuditService auditService;
    
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditService.getAllLogs());
    }
    
    @GetMapping("/range")
    public ResponseEntity<List<AuditLog>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(auditService.getLogsByDateRange(start, end));
    }
    
    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLog>> getLogsByEntity(@PathVariable String entityType, 
                                                           @PathVariable Long entityId) {
        return ResponseEntity.ok(auditService.getLogsByEntity(entityType, entityId));
    }
}
