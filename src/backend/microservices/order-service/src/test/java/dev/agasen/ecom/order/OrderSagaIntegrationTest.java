package dev.agasen.ecom.order;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.agasen.ecom.api.core.order.model.CreateOrderRequest;
import dev.agasen.ecom.api.core.order.model.OrderItem;
import dev.agasen.ecom.api.saga.order.events.InventoryEvent;
import dev.agasen.ecom.api.saga.order.events.PaymentEvent;

public class OrderSagaIntegrationTest extends BaseKafkaIntegTest {

  static final CreateOrderRequest REQUEST = CreateOrderRequest.builder()
    .customerId(1L)
    .items(List.of(
      new OrderItem(1L, 1, 100L),
      new OrderItem(2L, 2, 200L),
      new OrderItem(3L, 3, 300L)

    ))
    .build();

  @Test
  void orderCompletedWorkflow() {
    // order create request + validate order in pending state
    var orderIdRef = super.intiateOrder(REQUEST);

    // check for order created event
    super.verifyOrderCreatedEvent(orderIdRef, e -> {
      assert e.customerId().equals(REQUEST.getCustomerId());
      assert e.items().size() == REQUEST.getItems().size();
    });

    verifyOrderComponentsInPendingState(orderIdRef);

    // emit payment deducted event
    // we only need the orderId
    emitEvent(PaymentEvent.Processed.builder().orderId(orderIdRef).build());

    // emit inventory deducted event
    emitEvent(InventoryEvent.Deducted.builder().orderId(orderIdRef).build());

    // check for order completed event
    // this.verifyOrderComplettedEvent(orderIdRef);

    verifyOrderComponentsInCompletedState(orderIdRef);
  }

  @Test
  void orderRollbackWorkflow() {
    // order create request + validate order in pending state
    var orderIdRef = super.intiateOrder(REQUEST);

    // check for order created event
    super.verifyOrderCreatedEvent(orderIdRef, e -> {
      assert e.customerId().equals(REQUEST.getCustomerId());
      assert e.items().size() == REQUEST.getItems().size();
    });

    verifyOrderComponentsInPendingState(orderIdRef);

    /**
     * EMIT A PAYMENT DECLINED EVENT
     * THIS SHOULD CAUSE THE ORDER TO ROLLBACK
     */
    var declinedPaymentEvent = PaymentEvent.Declined.builder()
      .orderId(orderIdRef)
      .amount(
        REQUEST.getItems().stream()
          .mapToLong(i -> i.getQuantity() * (i.getPrice() == null ? 0 : i.getPrice()))
          .sum())
      .customerId(REQUEST.getCustomerId())
      .message("Insufficient funds")
      .build();

    emitEvent(declinedPaymentEvent);

    emitEvent(InventoryEvent.Deducted.builder().orderId(orderIdRef).build());

    // check for order cancelled event
    this.verifyOrderCancelledEvent(orderIdRef);

    // check for components - payment in failed state, inventory in processed state
    verifyPamentComponentInFailedState(orderIdRef);
    verifyInventoryComponentInProcessedState(orderIdRef);
  }

}
