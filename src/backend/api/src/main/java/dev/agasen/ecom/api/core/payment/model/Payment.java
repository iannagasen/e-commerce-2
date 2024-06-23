package dev.agasen.ecom.api.core.payment.model;

import java.time.LocalDateTime;

public interface Payment {

  Long getPaymentId();
  void setPaymentId(Long paymentId);
  
  Long getOrderId();
  void setOrderId(Long orderId);

  Long getCustomerId();
  void setCustomerId(Long customerId);

  Long getAmount();
  void setAmount(Long amount);

  TransactionType getTransactionType();
  void setTransactionType(TransactionType transactionType);

  LocalDateTime getTransactionDate();
  void setTransactionDate(LocalDateTime transactionDate);

}
