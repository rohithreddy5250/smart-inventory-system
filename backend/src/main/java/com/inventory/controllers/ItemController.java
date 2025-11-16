package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.entity.Item;
import com.inventory.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ItemController {
    
    private final ItemService itemService;
    
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }
    
    @GetMapping("/qr/{qrCode}")
    public ResponseEntity<Item> getItemByQRCode(@PathVariable String qrCode) {
        return ResponseEntity.ok(itemService.getItemByQRCode(qrCode));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam String query) {
        return ResponseEntity.ok(itemService.searchItems(query));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Item>> getItemsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(itemService.getItemsByCategory(category));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Item>> getItemsByStatus(@PathVariable Item.ItemStatus status) {
        return ResponseEntity.ok(itemService.getItemsByStatus(status));
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return ResponseEntity.ok(itemService.getAllCategories());
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse> createItem(@RequestBody Item item) {
        return ResponseEntity.ok(itemService.createItem(item));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse> updateItem(@PathVariable Long id, 
                                                   @RequestBody Item item) {
        return ResponseEntity.ok(itemService.updateItem(id, item));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.deleteItem(id));
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse> updateItemStatus(@PathVariable Long id, 
                                                         @RequestParam Item.ItemStatus status) {
        itemService.updateItemStatus(id, status);
        return ResponseEntity.ok(new ApiResponse(true, "Status updated successfully"));
    }
}
