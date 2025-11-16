package com.inventory.repository;

import com.inventory.entity.BorrowTransaction;
import com.inventory.entity.Item;
import com.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorrowTransactionRepository extends JpaRepository<BorrowTransaction, Long> {
    List<BorrowTransaction> findByUser(User user);
    List<BorrowTransaction> findByItem(Item item);
    List<BorrowTransaction> findByStatus(BorrowTransaction.TransactionStatus status);
    
    @Query("SELECT t FROM BorrowTransaction t WHERE t.status = 'ACTIVE' " +
           "AND t.expectedReturnDate < :date AND t.overdueReminderSent = false")
    List<BorrowTransaction> findOverdueTransactions(LocalDateTime date);
    
    @Query("SELECT t FROM BorrowTransaction t WHERE t.user.id = :userId " +
           "ORDER BY t.createdAt DESC")
    List<BorrowTransaction> findByUserId(Long userId);
    
    @Query("SELECT t FROM BorrowTransaction t WHERE t.item.id = :itemId " +
           "ORDER BY t.createdAt DESC")
    List<BorrowTransaction> findByItemId(Long itemId);
}
