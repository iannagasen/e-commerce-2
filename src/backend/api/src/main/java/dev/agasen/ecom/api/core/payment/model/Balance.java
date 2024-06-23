package dev.agasen.ecom.api.core.payment.model;

public interface Balance {

  Long getBalanceId();
  void setBalanceId(Long balanceId);
  
  Long getCustomerId();
  void setCustomerId(Long customerId);

  Long getAmount();
  void setAmount(Long amount);

}