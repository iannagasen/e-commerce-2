package dev.agasen.ecom.api.core.inventory.model;

import java.util.List;

import dev.agasen.ecom.api.core.order.model.OrderItem;
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
  private Long orderId;
  private Long customerId;
  private List<OrderItem> items;
}
