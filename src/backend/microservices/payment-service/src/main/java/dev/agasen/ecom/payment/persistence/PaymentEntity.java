package dev.agasen.ecom.payment.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import dev.agasen.ecom.api.core.payment.model.OrderItem;
import dev.agasen.ecom.api.core.payment.model.Payment;
import dev.agasen.ecom.api.core.payment.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="payment")
@Builder
public class PaymentEntity implements Payment  {
  
  public static final String SEQUENCE_NAME = "payment_sequence";

  private @Id String id;
  private @Indexed(unique=true) Long paymentId;
  private Long orderId;
  private Long customerId;
  private List<OrderItem> orderItems;
  private TransactionType transactionType;
  private LocalDateTime transactionDate;

  public Long totalAmount() {
    return orderItems.stream()
        .map(item -> item.getPrice() * item.getQuantity())
        .reduce(Long::sum)
        .orElse(Long.valueOf(0));
  }

  public static PaymentEntity newPayment(Long paymentId, Long orderId, Long customerId, List<OrderItem> orderItems) {
    return new PaymentEntity(null, paymentId, orderId, customerId, orderItems, TransactionType.PAYMENT, LocalDateTime.now());
  }

  public static PaymentEntity newRefund(Long paymentId, Long orderId, Long customerId, List<OrderItem> orderItems) {
    return new PaymentEntity(null, paymentId, orderId, customerId, orderItems, TransactionType.REFUND, LocalDateTime.now());
  }



}
