package dev.agasen.ecom.api.saga.order;

import dev.agasen.ecom.api.messaging.Saga;

public interface OrderSaga extends Saga {
  
  Long orderId();

}
