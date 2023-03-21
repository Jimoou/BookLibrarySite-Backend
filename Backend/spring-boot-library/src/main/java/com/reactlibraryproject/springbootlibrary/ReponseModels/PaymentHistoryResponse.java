package com.reactlibraryproject.springbootlibrary.ReponseModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentHistoryResponse {

  private String paymentKey;

  private String orderName;

  private int totalPrice;

  private String purchaseDate;

  private String orderId;

  private String status;
}
