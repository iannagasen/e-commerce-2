package dev.agasen.ecom.order;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
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

  static final CreateOrderRequest REQUEST2 = CreateOrderRequest.builder()
    .customerId(1L)
    .items(List.of(
      new OrderItem(1L, 1, 100L),
      new OrderItem(2L, 2, 200L),
      new OrderItem(3L, 3, 300L)

    ))
    .build();

  @Test
  @Order(1)
  void orderCompletedWorkflow() throws InterruptedException {
    // order create request + validate order in pending state
    var orderIdRef = super.intiateOrder(REQUEST);

    // check for order created event
    super.verifyOrderCreatedEvent(orderIdRef, e -> {
      assert e.customerId().equals(REQUEST.getCustomerId());
      assert e.items().size() == REQUEST.getItems().size();
    });

    verifyOrderComponentsInPendingState(orderIdRef);

    
    // emit inventory deducted event
    emitEvent(InventoryEvent.Deducted.builder().orderId(orderIdRef).build());

    Thread.sleep(4000);

    // emit payment deducted event
    emitEvent(PaymentEvent.Processed.builder().orderId(orderIdRef).build());

    // Lets wait for some time to make sure Stream Bridge has enough time to process the events
    // and then we can verify the order completed event
    Thread.sleep(4000);

    // check for order completed event
    // this needs to be done first before verifyOrderComponentsInCompletedState
    //      since we want to make sure Order Completted is propagated
    this.verifyOrderComplettedEvent(orderIdRef);

    Thread.sleep(4000);

    // verifyOrderComponentsInCompletedState(orderIdRef);
  }

  @Test
  @Order(2)
  void orderRollbackWorkflow() throws InterruptedException {
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

    // Lets wait for some time to make sure Stream Bridge has enough time to process the events
    // and then we can verify the order completed event
    Thread.sleep(4000);

    // check for order cancelled event
    this.verifyOrderCancelledEvent(orderIdRef);

    // check for components - payment in failed state, inventory in processed state
    verifyPamentComponentInFailedState(orderIdRef);
    verifyInventoryComponentInProcessedState(orderIdRef);
  }

}
