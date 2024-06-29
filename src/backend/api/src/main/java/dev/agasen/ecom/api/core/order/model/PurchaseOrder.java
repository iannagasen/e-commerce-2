package dev.agasen.ecom.api.core.order.model;

import java.util.List;

import dev.agasen.ecom.api.saga.order.status.OrderStatus;
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
public class PurchaseOrder {
  private Long orderId;
  private Long customerId;
  private List<OrderItem> items;
  private OrderStatus orderStatus;
}
