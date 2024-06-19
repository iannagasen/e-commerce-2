package dev.agasen.ecom.api.core.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryDeductionRequest {
  private Long productId;
  private Long orderId;
  private Long customerId;
  private int quantity;
}
