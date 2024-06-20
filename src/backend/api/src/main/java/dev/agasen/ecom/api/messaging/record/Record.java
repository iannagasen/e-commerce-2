package dev.agasen.ecom.api.messaging.record;

import reactor.kafka.receiver.ReceiverOffset;

public record Record<T>(
  String key,
  T message,
  ReceiverOffset acknowledgement
) {
  
}