package dev.agasen.ecom.api.core.inventory.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class InventoryUpdate { 

  private Long updateId;
  private Long inventoryId;
  private Long orderId;
  private InventoryUpdateType type;
  private int quantity;
  private LocalDateTime createdAt;
  
}
