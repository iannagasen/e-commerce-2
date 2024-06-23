package dev.agasen.ecom.api.core.order.model;

import java.util.List;

import dev.agasen.ecom.api.saga.order.status.OrderStatus;

public interface PurchaseOrder {
  Long getOrderId();
  void setOrderId(Long orderId);

  Long getCustomerId();
  void setCustomerId(Long customerId);

  List<OrderItem> getItems();
  void setItems(List<OrderItem> items);

  OrderStatus getOrderStatus();
  void setOrderStatus(OrderStatus orderStatus);

}
