package dev.agasen.ecom.api.core.payment.model;

import java.time.LocalDateTime;
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
public class Payment {

  private Long paymentId;
  private Long orderId;
  private Long customerId;
  private List<OrderItem> orderItems;
  private TransactionType transactionType;
  private LocalDateTime transactionDate;

}
