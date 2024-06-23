package dev.agasen.ecom.order.persistence;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import dev.agasen.ecom.api.core.order.model.OrderItem;
import dev.agasen.ecom.api.core.order.model.PurchaseOrder;
import dev.agasen.ecom.api.saga.order.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection="purchase_order")
public class PurchaseOrderEntity implements PurchaseOrder {

  public static final String SEQUENCE_NAME = "purchase_order_sequence";

  private @Id String id;
  private @Indexed(unique=true) Long orderId;
  private Long customerId;
  private List<OrderItem> items;
  private OrderStatus orderStatus;

  public List<OrderItem> getItems() {
    return items == null ? List.of() : items;
  }

  public static PurchaseOrderEntity fromPending(Long orderId, Long customerId, List<OrderItem> items) {
    return PurchaseOrderEntity.builder()
      .orderId(orderId)
      .customerId(customerId)
      .items(items)
      .orderStatus(OrderStatus.PENDING)
      .build();
  }
  
}
