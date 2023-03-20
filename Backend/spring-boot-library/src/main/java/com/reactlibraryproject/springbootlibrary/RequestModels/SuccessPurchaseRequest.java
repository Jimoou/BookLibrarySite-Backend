package com.reactlibraryproject.springbootlibrary.RequestModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessPurchaseRequest {
    private String paymentKey;
    private String orderId;
    private String purchaseDate;
    private String status;
    private int totalAmount;
}
