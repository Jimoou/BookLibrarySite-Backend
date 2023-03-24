package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.CustomExceptions.TossPayResponseException;
import com.reactlibraryproject.springbootlibrary.DAO.CoinRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Coin;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPaymentRequest;
import com.reactlibraryproject.springbootlibrary.Utils.TossPay;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CoinService {
    private CoinRepository coinRepository;

    public int userCoins(String userEmail) {
        Coin coin = coinRepository.findByUserEmail(userEmail);
        return coin.getAmount();
    }

    public ResponseEntity<String> confirmPayment(String userEmail, SuccessPaymentRequest paymentRequests) {
        int amount = calculateCoin(paymentRequests.getAmount());
        ResponseEntity<String> response = TossPay.pay(paymentRequests);
        if (response.getStatusCodeValue() == 200) {
            chargeCoin(userEmail, amount);
        } else {
            throw new TossPayResponseException(response);
        }
        return response;
    }

    private int calculateCoin(int amount) {
        return switch (amount) {
            case 5000 -> 50;
            case 10000 -> 120;
            case 15000 -> 180;
            default -> amount;
        };
    }

    private void chargeCoin(String userEmail, int amount) {
        Coin coin = coinRepository.findByUserEmail(userEmail);
        if (coin == null) {
            coin = Coin.builder()
             .userEmail(userEmail)
             .amount(amount)
             .build();
        } else {
            coin.setAmount(coin.getAmount() + amount);
        }
        coinRepository.save(coin);
    }

    public void useCoin(String userEmail, int coinsToUse) throws Exception {
        Coin coin = coinRepository.findByUserEmail(userEmail);
        if (coin.getAmount() <= 0) {
            throw new Exception("코인이 부족합니다.");
        }
        coin.setAmount(coin.getAmount() - coinsToUse);
        coinRepository.save(coin);
    }
}
