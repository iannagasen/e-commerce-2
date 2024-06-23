package dev.agasen.ecom.order.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import dev.agasen.ecom.api.saga.order.status.ParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Document(collection="order_components")
@Getter
@NoArgsConstructor
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

  @Getter   
  @SuperBuilder
  @TypeAlias("order_inventory")
  @Document(collection="order_component")
  @AllArgsConstructor
  public static class Inventory extends OrderComponentEntity {
    private Long inventoryId;
    private @Setter ParticipantStatus status;
    private @Setter boolean successful;
    private @Setter String message;

    public String getComponentName() {
      return "order_inventory";
    }
  }

  @Getter
  @SuperBuilder
  @TypeAlias("order_payment")
  @Document(collection="order_component")
  @AllArgsConstructor
  public static class Payment extends OrderComponentEntity {
    private Long paymentId;
    private @Setter ParticipantStatus status;
    private @Setter boolean successful;
    private @Setter String message;

    public String getComponentName() {
      return "order_payment";
    }
  }
  
}
