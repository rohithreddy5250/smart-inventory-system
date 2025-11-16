package com.inventory.repository;

import com.inventory.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByQrCode(String qrCode);
    Optional<Item> findByItemCode(String itemCode);
    List<Item> findByCategory(String category);
    List<Item> findByStatus(Item.ItemStatus status);
    
    @Query("SELECT DISTINCT i.category FROM Item i")
    List<String> findAllCategories();
    
    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(i.itemCode) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Item> searchItems(String search);
}
