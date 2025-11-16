package com.inventory.service;

import com.inventory.dto.DashboardStats;
import com.inventory.entity.BorrowTransaction;
import com.inventory.entity.Item;
import com.inventory.repository.BorrowTransactionRepository;
import com.inventory.repository.ItemRepository;
import com.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BorrowTransactionRepository transactionRepository;
    
    public DashboardStats getDashboardStats() {
        long totalItems = itemRepository.count();
        long availableItems = itemRepository.findByStatus(Item.ItemStatus.AVAILABLE).size();
        long borrowedItems = itemRepository.findByStatus(Item.ItemStatus.BORROWED).size();
        long overdueItems = transactionRepository
                .findOverdueTransactions(LocalDateTime.now()).size();
        long totalUsers = userRepository.count();
        long activeTransactions = transactionRepository
                .findByStatus(BorrowTransaction.TransactionStatus.ACTIVE).size();
        
        return new DashboardStats(totalItems, availableItems, borrowedItems, 
                overdueItems, totalUsers, activeTransactions);
    }
}
