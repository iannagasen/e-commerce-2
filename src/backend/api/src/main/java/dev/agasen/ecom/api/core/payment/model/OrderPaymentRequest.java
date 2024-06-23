package dev.agasen.ecom.api.core.payment.model;

import java.util.List;

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
        .mapToLong(item -> item.getPrice() * item.getQuantity())
        .sum();
  }

}
