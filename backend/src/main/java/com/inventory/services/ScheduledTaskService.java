package com.inventory.service;

import com.inventory.entity.BorrowTransaction;
import com.inventory.repository.BorrowTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {
    
    private final BorrowTransactionRepository transactionRepository;
    private final AuditService auditService;
    
    // Run every day at 9 AM
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void checkOverdueItems() {
        log.info("Checking for overdue items...");
        
        List<BorrowTransaction> overdueTransactions = 
                transactionRepository.findOverdueTransactions(LocalDateTime.now());
        
        for (BorrowTransaction transaction : overdueTransactions) {
            if (!transaction.isOverdueReminderSent()) {
                transaction.setStatus(BorrowTransaction.TransactionStatus.OVERDUE);
                transaction.setOverdueReminderSent(true);
                transactionRepository.save(transaction);
                
                // Log the overdue item
                auditService.log("ITEM_OVERDUE", "BorrowTransaction", 
                        transaction.getId(),
                        String.format("Item %s is overdue. User: %s", 
                                transaction.getItem().getName(),
                                transaction.getUser().getUsername()));
                
                // Here you can add email notification logic
                sendOverdueReminder(transaction);
                
                log.info("Overdue reminder sent for transaction ID: {}", transaction.getId());
            }
        }
        
        log.info("Overdue check completed. Found {} overdue items.", overdueTransactions.size());
    }
    
    private void sendOverdueReminder(BorrowTransaction transaction) {
        // TODO: Implement email sending logic
        // You can use JavaMailSender here
        log.info("Sending overdue reminder to: {}", transaction.getUser().getEmail());
    }
}
