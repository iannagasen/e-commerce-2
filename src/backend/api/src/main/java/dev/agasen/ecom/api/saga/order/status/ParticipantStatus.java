package dev.agasen.ecom.api.saga.order.status;

public enum ParticipantStatus {
  PENDING, // process was initiated, messages are sent to participants
  PROCESSED, // participant has processed the message and successful
  FAILED, // something went wrong on the participant side - next step is to rollback
  ROLLBACK, // rollback because one participant failed
  COMPLETED // all participants have processed the message successfully
}