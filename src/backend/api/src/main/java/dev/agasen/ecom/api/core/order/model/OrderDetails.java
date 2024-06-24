package dev.agasen.ecom.api.core.order.model;

import java.util.List;

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
public class OrderDetails {
  private PurchaseOrder purchaseOrder;
  private List<OrderItem> orderItems;
  private Long totalPayment;
}
