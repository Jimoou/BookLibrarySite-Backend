package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.CoinRepository;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPaymentRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CoinService {
    private CoinRepository coinRepository;

    public int userCoins(String userEmail) {
        return 0;
    }

    public ResponseEntity<String> successPayment(String userEmail, SuccessPaymentRequest paymentRequests) {
        return null;
    }

    public void payWithCoin(String userEmail) {
    }
}
