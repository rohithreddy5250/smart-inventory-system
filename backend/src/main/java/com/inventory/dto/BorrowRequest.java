package com.inventory.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BorrowRequest {
    private Long itemId;
    private LocalDateTime expectedReturnDate;
    private String purpose;
    private String notes;
}
