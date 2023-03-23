package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.CoinRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.Coin;
import com.reactlibraryproject.springbootlibrary.Entity.PaymentHistory;
import com.reactlibraryproject.springbootlibrary.RequestModels.PendingPaymentRequest;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPaymentRequest;
import com.reactlibraryproject.springbootlibrary.Utils.TossPay;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class CoinService {
    private CoinRepository coinRepository;

    public int userCoins(String userEmail) {
        Coin coin = coinRepository.findByUserEmail(userEmail);
        return coin.getAmount();
    }

    public ResponseEntity<String> successPayment(String userEmail, SuccessPaymentRequest paymentRequests, int amount) throws IOException, InterruptedException {
        ResponseEntity<String> response = TossPay.pay(paymentRequests);
        Coin coin = coinRepository.findByUserEmail(userEmail);
        if (coin == null){
            coin = Coin.builder()
             .userEmail(userEmail)
             .amount(amount)
             .build();
        } else {
            coin.setAmount(coin.getAmount() + amount);
        }
        coinRepository.save(coin);
        return response;
    }

    private void addPendingPayment(
     String userEmail, int amount) {
    }

    public void useCoin(String userEmail, int coinsToUse) {
        Coin coin = coinRepository.findByUserEmail(userEmail);
        coin.setAmount(coin.getAmount() - coinsToUse);
        coinRepository.save(coin);
    }
}
