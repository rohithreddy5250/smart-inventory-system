package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStats {
    private long totalItems;
    private long availableItems;
    private long borrowedItems;
    private long overdueItems;
    private long totalUsers;
    private long activeTransactions;
}
