package com.inventory.service;

import com.inventory.dto.ApiResponse;
import com.inventory.entity.Item;
import com.inventory.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final AuditService auditService;
    
    @Transactional
    public ApiResponse createItem(Item item) {
        if (item.getItemCode() == null || item.getItemCode().isEmpty()) {
            item.setItemCode(generateItemCode());
        }
        
        if (item.getQrCode() == null || item.getQrCode().isEmpty()) {
            item.setQrCode(generateQRCode());
        }
        
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        
        Item savedItem = itemRepository.save(item);
        auditService.log("ITEM_CREATED", "Item", savedItem.getId(), 
                "Created item: " + savedItem.getName());
        
        return new ApiResponse(true, "Item created successfully", savedItem);
    }
    
    @Transactional
    public ApiResponse updateItem(Long id, Item itemDetails) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        
        String oldValue = itemToString(item);
        
        item.setName(itemDetails.getName());
        item.setDescription(itemDetails.getDescription());
        item.setCategory(itemDetails.getCategory());
        item.setLocation(itemDetails.getLocation());
        item.setPurchasePrice(itemDetails.getPurchasePrice());
        item.setPurchaseDate(itemDetails.getPurchaseDate());
        item.setVendor(itemDetails.getVendor());
        item.setQuantity(itemDetails.getQuantity());
        item.setUpdatedAt(LocalDateTime.now());
        
        Item updatedItem = itemRepository.save(item);
        
        auditService.logWithChanges("ITEM_UPDATED", "Item", updatedItem.getId(), 
                "Updated item: " + updatedItem.getName(), oldValue, itemToString(updatedItem));
        
        return new ApiResponse(true, "Item updated successfully", updatedItem);
    }
    
    @Transactional
    public ApiResponse deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        
        itemRepository.delete(item);
        auditService.log("ITEM_DELETED", "Item", id, 
                "Deleted item: " + item.getName());
        
        return new ApiResponse(true, "Item deleted successfully");
    }
    
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }
    
    public Item getItemByQRCode(String qrCode) {
        return itemRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }
    
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    
    public List<Item> searchItems(String search) {
        return itemRepository.searchItems(search);
    }
    
    public List<Item> getItemsByCategory(String category) {
        return itemRepository.findByCategory(category);
    }
    
    public List<Item> getItemsByStatus(Item.ItemStatus status) {
        return itemRepository.findByStatus(status);
    }
    
    public List<String> getAllCategories() {
        return itemRepository.findAllCategories();
    }
    
    @Transactional
    public void updateItemStatus(Long itemId, Item.ItemStatus status) {
        Item item = getItemById(itemId);
        item.setStatus(status);
        item.setUpdatedAt(LocalDateTime.now());
        itemRepository.save(item);
        
        auditService.log("ITEM_STATUS_CHANGED", "Item", itemId, 
                "Status changed to: " + status);
    }
    
    private String generateItemCode() {
        return "ITM-" + System.currentTimeMillis();
    }
    
    private String generateQRCode() {
        return UUID.randomUUID().toString();
    }
    
    private String itemToString(Item item) {
        return String.format("Name: %s, Category: %s, Status: %s, Location: %s",
                item.getName(), item.getCategory(), item.getStatus(), item.getLocation());
    }
}
