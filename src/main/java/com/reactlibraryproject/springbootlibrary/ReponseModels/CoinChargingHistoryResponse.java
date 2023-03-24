package com.reactlibraryproject.springbootlibrary.ReponseModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CoinChargingHistoryResponse {

    private Long id;

    private int amount;

    private int price;

    private String paymentDate;

}
