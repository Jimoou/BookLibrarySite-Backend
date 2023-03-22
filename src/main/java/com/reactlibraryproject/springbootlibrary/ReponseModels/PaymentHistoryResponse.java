package com.reactlibraryproject.springbootlibrary.ReponseModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentHistoryResponse {

  private String title;

  private String author;

  private String category;

  private String img;

  private String publisher;

  private int amount;

  private int price;

  private String paymentDate;

  private String orderId;

}
