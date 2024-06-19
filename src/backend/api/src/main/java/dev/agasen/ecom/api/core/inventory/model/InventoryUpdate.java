package dev.agasen.ecom.api.core.inventory.model;

import java.time.LocalDateTime;


public interface InventoryUpdate { 

  Long getUpdateId();
  void setUpdateId(Long id);

  Long getInventoryId();
  void setInventoryId(Long inventoryId);

  Long getOrderId();
  void setOrderId(Long orderId);

  InventoryUpdateType getType();
  void setType(InventoryUpdateType type);

  int getQuantity();
  void setQuantity(int quantity);

  LocalDateTime getCreatedAt();
  void setCreatedAt(LocalDateTime createdAt);

  
}
