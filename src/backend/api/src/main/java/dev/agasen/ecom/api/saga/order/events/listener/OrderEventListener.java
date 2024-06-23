package dev.agasen.ecom.api.saga.order.events.listener;

import dev.agasen.ecom.api.core.order.model.PurchaseOrder;

public interface OrderEventListener {
  
  void emitOrderCreatedEvent(PurchaseOrder req);

}
