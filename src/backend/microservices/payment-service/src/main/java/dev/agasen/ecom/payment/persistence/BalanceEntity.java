package dev.agasen.ecom.payment.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import dev.agasen.ecom.api.core.payment.model.Balance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="balance")
public class BalanceEntity implements Balance {

  public static final String SEQUENCE_NAME = "balance_sequence";

  private @Id String id;
  private @Indexed(unique=true) Long balanceId;
  private @Indexed Long customerId;
  private Long amount;
}
