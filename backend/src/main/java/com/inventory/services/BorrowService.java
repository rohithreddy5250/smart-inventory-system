package com.inventory.service;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.BorrowRequest;
import com.inventory.entity.BorrowTransaction;
import com.inventory.entity.Item;
import com.inventory.entity.User;
import com.inventory.repository.BorrowTransactionRepository;
import com.inventory.repository.ItemRepository;
import com.inventory.repository.UserRepository;
import com.inventory.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {
    
    private final BorrowTransactionRepository transactionRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final AuditService auditService;
    
    @Transactional
    public ApiResponse createBorrowRequest(BorrowRequest request) {
        User currentUser = getCurrentUser();
        Item item = itemService.getItemById(request.getItemId());
        
        if (item.getStatus() != Item.ItemStatus.AVAILABLE) {
            return new ApiResponse(false, "Item is not available for borrowing");
        }
        
        BorrowTransaction transaction = new BorrowTransaction();
        transaction.setItem(item);
        transaction.setUser(currentUser);
        transaction.setBorrowDate(LocalDateTime.now());
        transaction.setExpectedReturnDate(request.getExpectedReturnDate());
        transaction.setPurpose(request.getPurpose());
        transaction.setNotes(request.getNotes());
        transaction.setStatus(BorrowTransaction.TransactionStatus.PENDING);
        
        BorrowTransaction savedTransaction = transactionRepository.save(transaction);
        
        auditService.log("BORROW_REQUEST_CREATED", "BorrowTransaction", 
                savedTransaction.getId(), 
                String.format("User %s requested to borrow item %s", 
                        currentUser.getUsername(), item.getName()));
        
        return new ApiResponse(true, "Borrow request created successfully", savedTransaction);
    }
    
    @Transactional
    public ApiResponse approveBorrowRequest(Long transactionId) {
        User approver = getCurrentUser();
        BorrowTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (transaction.getStatus() != BorrowTransaction.TransactionStatus.PENDING) {
            return new ApiResponse(false, "Transaction is not in pending status");
        }
        
        transaction.setStatus(BorrowTransaction.TransactionStatus.ACTIVE);
        transaction.setApprovedBy(approver);
        transactionRepository.save(transaction);
        
        itemService.updateItemStatus(transaction.getItem().getId(), Item.ItemStatus.BORROWED);
        
        auditService.log("BORROW_REQUEST_APPROVED", "BorrowTransaction", 
                transactionId, 
                String.format("Approved by %s for user %s", 
                        approver.getUsername(), transaction.getUser().getUsername()));
        
        return new ApiResponse(true, "Borrow request approved", transaction);
    }
    
    @Transactional
    public ApiResponse rejectBorrowRequest(Long transactionId, String reason) {
        User approver = getCurrentUser();
        BorrowTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (transaction.getStatus() != BorrowTransaction.TransactionStatus.PENDING) {
            return new ApiResponse(false, "Transaction is not in pending status");
        }
        
        transaction.setStatus(BorrowTransaction.TransactionStatus.REJECTED);
        transaction.setApprovedBy(approver);
        transaction.setNotes(transaction.getNotes() + "\nRejection reason: " + reason);
        transactionRepository.save(transaction);
        
        auditService.log("BORROW_REQUEST_REJECTED", "BorrowTransaction", 
                transactionId, 
                String.format("Rejected by %s. Reason: %s", approver.getUsername(), reason));
        
        return new ApiResponse(true, "Borrow request rejected", transaction);
    }
    
    @Transactional
    public ApiResponse returnItem(Long transactionId, String returnCondition) {
        BorrowTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (transaction.getStatus() != BorrowTransaction.TransactionStatus.ACTIVE &&
            transaction.getStatus() != BorrowTransaction.TransactionStatus.OVERDUE) {
            return new ApiResponse(false, "Invalid transaction status for return");
        }
        
        transaction.setActualReturnDate(LocalDateTime.now());
        transaction.setReturnCondition(returnCondition);
        transaction.setStatus(BorrowTransaction.TransactionStatus.RETURNED);
        transactionRepository.save(transaction);
        
        itemService.updateItemStatus(transaction.getItem().getId(), Item.ItemStatus.AVAILABLE);
        
        auditService.log("ITEM_RETURNED", "BorrowTransaction", transactionId, 
                String.format("Item %s returned by %s. Condition: %s", 
                        transaction.getItem().getName(), 
                        transaction.getUser().getUsername(), 
                        returnCondition));
        
        return new ApiResponse(true, "Item returned successfully", transaction);
    }
    
    public List<BorrowTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public List<BorrowTransaction> getMyTransactions() {
        User currentUser = getCurrentUser();
        return transactionRepository.findByUser(currentUser);
    }
    
    public List<BorrowTransaction> getPendingTransactions() {
        return transactionRepository.findByStatus(BorrowTransaction.TransactionStatus.PENDING);
    }
    
    public List<BorrowTransaction> getActiveTransactions() {
        return transactionRepository.findByStatus(BorrowTransaction.TransactionStatus.ACTIVE);
    }
    
    public List<BorrowTransaction> getOverdueTransactions() {
        return transactionRepository.findOverdueTransactions(LocalDateTime.now());
    }
    
    public BorrowTransaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
    
    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
