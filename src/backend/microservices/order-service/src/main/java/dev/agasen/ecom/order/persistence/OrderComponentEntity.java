package dev.agasen.ecom.order.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import dev.agasen.ecom.api.saga.order.status.ParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Document(collection="order_components")
@Getter
@NoArgsConstructor
@ToString
public abstract class OrderComponentEntity {

  public static final String SEQUENCE_NAME = "order_component_sequence";

  protected @Id String id;
  protected @Indexed(unique=true) Long componentId;
  protected @Indexed(unique=true) Long orderId;

  public abstract String getComponentName();
  public abstract boolean isSuccessful();
  public abstract void setSuccessful(boolean successful);
  public abstract String getMessage();
  public abstract void setMessage(String message);
  public abstract ParticipantStatus getStatus();
  public abstract void setStatus(ParticipantStatus status);

  protected OrderComponentEntity(String id, Long componentId, Long orderId) {
    this.id = id;
    this.componentId = componentId;
    this.orderId = orderId;
  }

  @Getter   
  @TypeAlias("order_inventory")
  @Document(collection="order_component")
  @AllArgsConstructor
  @ToString
  public static class Inventory extends OrderComponentEntity {
    private @Setter ParticipantStatus status;
    private @Setter boolean successful;
    private @Setter String message;

    @PersistenceCreator
    public Inventory(
        String id, Long componentId, Long orderId,
        ParticipantStatus status, boolean successful, String message
    ) {
      super(id, componentId, orderId);
      this.status = status;
      this.successful = successful;
      this.message = message;
    }

    public String getComponentName() {
      return "order_inventory";
    }

    public static Inventory newSuccessful(Long componentId, ParticipantStatus status, Long orderId, String message) {
      return new Inventory(null, componentId, orderId, status, true, message);
    }

    public static Inventory newUnsuccessful(Long componentId, ParticipantStatus status, Long orderId, String message) {
      return new Inventory(null, componentId, orderId, status, false, message);
    }
  }

  @Getter
  @TypeAlias("order_payment")
  @Document(collection="order_component")
  @ToString
  public static class Payment extends OrderComponentEntity {
    private @Setter ParticipantStatus status;
    private @Setter boolean successful;
    private @Setter String message;

    @PersistenceCreator
    public Payment(
        String id, Long componentId, Long orderId,
        ParticipantStatus status, boolean successful, String message
    ) {
      super(id, componentId, orderId);
      this.status = status;
      this.successful = successful;
      this.message = message;
    }

    public String getComponentName() {
      return "order_payment";
    }

    public static Payment newSuccessful(Long componentId, ParticipantStatus status, Long orderId, String message) {
      return new Payment(null, componentId, orderId, status, true, message);
    }

    public static Payment newUnsuccessful(Long componentId, ParticipantStatus status, Long orderId, String message) {
      return new Payment(null, componentId, orderId, status, false, message);
    }
  }
  
}
