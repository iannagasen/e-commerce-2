package dev.agasen.ecom.api.core.payment.model;

import java.time.LocalDateTime;
import java.util.List;

import dev.agasen.ecom.api.core.order.model.OrderItem;

public interface Payment {

  Long getPaymentId();
  void setPaymentId(Long paymentId);
  
  Long getOrderId();
  void setOrderId(Long orderId);

  Long getCustomerId();
  void setCustomerId(Long customerId);

  List<OrderItem> getOrderItems();
  void setOrderItems(List<OrderItem> orderItems);

  TransactionType getTransactionType();
  void setTransactionType(TransactionType transactionType);

  LocalDateTime getTransactionDate();
  void setTransactionDate(LocalDateTime transactionDate);

}
