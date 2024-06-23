package dev.agasen.ecom.payment.persistence;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import dev.agasen.ecom.api.core.payment.model.Payment;
import dev.agasen.ecom.api.core.payment.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="payment")
public class PaymentEntity implements Payment  {
  
  public static final String SEQUENCE_NAME = "payment_sequence";

  private @Id String id;
  private @Indexed(unique=true) Long paymentId;
  private Long orderId;
  private Long customerId;
  private Long amount;
  private TransactionType transactionType;
  private LocalDateTime transactionDate;

}
