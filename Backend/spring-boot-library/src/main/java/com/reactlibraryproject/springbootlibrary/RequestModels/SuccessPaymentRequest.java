package com.reactlibraryproject.springbootlibrary.RequestModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessPaymentRequest {
  private String paymentKey;
  private String orderId;
  private String paymentDate;
  private String status;
  private int totalAmount;
}
