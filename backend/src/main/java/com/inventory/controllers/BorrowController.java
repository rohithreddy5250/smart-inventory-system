package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.BorrowRequest;
import com.inventory.entity.BorrowTransaction;
import com.inventory.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BorrowController {
    
    private final BorrowService borrowService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<BorrowTransaction>> getAllTransactions() {
        return ResponseEntity.ok(borrowService.getAllTransactions());
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<BorrowTransaction>> getMyTransactions() {
        return ResponseEntity.ok(borrowService.getMyTransactions());
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<BorrowTransaction>> getPendingTransactions() {
        return ResponseEntity.ok(borrowService.getPendingTransactions());
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<BorrowTransaction>> getActiveTransactions() {
        return ResponseEntity.ok(borrowService.getActiveTransactions());
    }
    
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<BorrowTransaction>> getOverdueTransactions() {
        return ResponseEntity.ok(borrowService.getOverdueTransactions());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BorrowTransaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(borrowService.getTransactionById(id));
    }
    
    @PostMapping("/request")
    public ResponseEntity<ApiResponse> createBorrowRequest(@RequestBody BorrowRequest request) {
        return ResponseEntity.ok(borrowService.createBorrowRequest(request));
    }
    
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse> approveBorrowRequest(@PathVariable Long id) {
        return ResponseEntity.ok(borrowService.approveBorrowRequest(id));
    }
    
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse> rejectBorrowRequest(@PathVariable Long id, 
                                                            @RequestParam String reason) {
        return ResponseEntity.ok(borrowService.rejectBorrowRequest(id, reason));
    }
    
    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse> returnItem(@PathVariable Long id, 
                                                   @RequestParam String condition) {
        return ResponseEntity.ok(borrowService.returnItem(id, condition));
    }
}
