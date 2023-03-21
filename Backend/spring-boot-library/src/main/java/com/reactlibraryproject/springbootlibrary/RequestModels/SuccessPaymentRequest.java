package com.reactlibraryproject.springbootlibrary.RequestModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessPaymentRequest {
    private String paymentKey;
    private int amount;
    private String orderId;
}
