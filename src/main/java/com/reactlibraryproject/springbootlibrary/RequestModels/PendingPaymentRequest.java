package com.reactlibraryproject.springbootlibrary.RequestModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PendingPaymentRequest {

  private Long bookId;
  private int amount;
  private Long cartItemId;
}
