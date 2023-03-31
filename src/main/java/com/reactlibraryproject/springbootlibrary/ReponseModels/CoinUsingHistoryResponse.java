package com.reactlibraryproject.springbootlibrary.ReponseModels;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CoinUsingHistoryResponse {
    private Long id;
    private String title;
    private int amount;
    private int balance;
    private String checkoutDate;
}
