package dev.agasen.ecom.api.messaging;

import java.time.Instant;

public interface DomainEvent {
  Instant createdAt();
}
