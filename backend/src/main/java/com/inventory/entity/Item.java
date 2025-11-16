package com.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String itemCode;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String category;

    @Column(unique = true, nullable = false)
    private String qrCode;

    @Enumerated(EnumType.STRING)
    private ItemStatus status = ItemStatus.AVAILABLE;

    private String location;
    private Double purchasePrice;
    private LocalDateTime purchaseDate;
    private String vendor;
    private Integer quantity = 1;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum ItemStatus {
        AVAILABLE, BORROWED, MAINTENANCE, LOST, DAMAGED
    }
}
