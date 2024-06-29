package dev.agasen.ecom.api.core.payment.model;

import java.util.List;

import dev.agasen.ecom.api.core.order.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPaymentRequest {
  
  private Long orderId;
  private Long customerId;
  private List<OrderItem> orderItems;

  public Long totalAmount() {
    return orderItems.stream()
        // TODO: Handling now since price are somewhat null
        // PRICE SHOULD NOT BE NULL !!!
        .mapToLong(item -> (item.getPrice() != null ? item.getPrice() : 0) * item.getQuantity())
        .sum();
  }

}
