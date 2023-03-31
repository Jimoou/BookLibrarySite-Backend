package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.CustomExceptions.InsufficientCoinsException;
import com.reactlibraryproject.springbootlibrary.DAO.CoinChargingHistoryRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CoinRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CoinUsingHistoryRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Coin;
import com.reactlibraryproject.springbootlibrary.Entity.CoinChargingHistory;
import com.reactlibraryproject.springbootlibrary.Entity.CoinUsingHistory;
import com.reactlibraryproject.springbootlibrary.ReponseModels.CoinChargingHistoryResponse;
import com.reactlibraryproject.springbootlibrary.ReponseModels.CoinUsingHistoryResponse;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPaymentRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CoinService {
    private CoinRepository coinRepository;
    private final CoinChargingHistoryRepository coinChargingHistoryRepository;
    private final CoinUsingHistoryRepository coinUsingHistoryRepository;


    /*유저의 코인 수*/
    public int userCoins(String userEmail) {
        Coin coin = coinRepository.findByUserEmail(userEmail);
        return coin != null ? coin.getAmount() : 0;
    }

    /*결제 승인 api 호출 후 결과 처리*/
    public void coinPayment(String userEmail, SuccessPaymentRequest paymentRequest) {
        int amount = calculateCoin(paymentRequest.getAmount());
        chargeCoin(userEmail, amount);
        saveCoinPaymentHistory(userEmail, paymentRequest, amount);
    }

    /*코인 충전 내역 저장*/
    private void saveCoinPaymentHistory(String userEmail, SuccessPaymentRequest paymentRequest, int amount) {
        CoinChargingHistory coinChargingHistory = CoinChargingHistory.builder()
         .userEmail(userEmail)
         .amount(amount)
         .price(paymentRequest.getAmount())
         .paymentKey(paymentRequest.getPaymentKey())
         .paymentDate(String.valueOf(LocalDateTime.now()))
         .orderId(paymentRequest.getOrderId())
         .status("DONE")
         .build();
        coinChargingHistoryRepository.save(coinChargingHistory);
    }

    private int calculateCoin(int amount) {
        return switch (amount) {
            case 5000 -> 50;
            case 10000 -> 120;
            case 15000 -> 180;
            default -> amount;
        };
    }

    /*코인 충전*/
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

    /*코인 사용*/
    public void useCoin(String userEmail, String bookTitle, int coinsToUse, String checkoutDate) {
        Coin coin = coinRepository.findByUserEmail(userEmail);
        if (coin.getAmount() <= 0) {
            throw new InsufficientCoinsException();
        }
        coin.setAmount(coin.getAmount() - coinsToUse);
        saveCoinUsingHistory(userEmail, coin.getAmount(), bookTitle, coinsToUse, checkoutDate);
        coinRepository.save(coin);
    }

    /*코인 사용 내역 저장*/
    private void saveCoinUsingHistory(String userEmail, int balance, String bookTitle, int coinsToUse, String checkoutDate) {
        CoinUsingHistory coinUsingHistory = CoinUsingHistory.builder()
         .userEmail(userEmail)
         .amount(coinsToUse)
         .balance(balance)
         .checkoutDate(checkoutDate)
         .bookTitle(bookTitle)
         .build();
        coinUsingHistoryRepository.save(coinUsingHistory);
    }

    /*코인 사용 내역 조회*/
    public List<CoinUsingHistoryResponse> getCoinUsingHistory(String userEmail) {
        List<CoinUsingHistory> coinUsingHistoryList = coinUsingHistoryRepository.findByUserEmail(userEmail);
        return castToUsingHistoryResponse(coinUsingHistoryList);
    }

    /*코인 사용 내역 응답 모델로 변환*/
    private List<CoinUsingHistoryResponse> castToUsingHistoryResponse(List<CoinUsingHistory> history) {
        history.sort(Comparator.comparing(CoinUsingHistory::getCheckoutDate).reversed());
        return
         history.stream()
          .map(
           detail ->
            new CoinUsingHistoryResponse(
             detail.getId(),
             detail.getBookTitle(),
             detail.getAmount(),
             detail.getBalance(),
             detail.getCheckoutDate()
            )).collect(Collectors.toList());
    }

    /*코인 충전 내역 조회*/
    public List<CoinChargingHistoryResponse> getCoinChargingHistory(String userEmail) {
        List<CoinChargingHistory> coinChargingHistoryList = coinChargingHistoryRepository.findByUserEmail(userEmail);
        return castToChargingHistoryResponse(coinChargingHistoryList);
    }

    /*코인 충전 내역 응답 모델로 변환*/
    private List<CoinChargingHistoryResponse> castToChargingHistoryResponse(List<CoinChargingHistory> history) {
        history.sort(Comparator.comparing(CoinChargingHistory::getPaymentDate).reversed());
        return
         history.stream()
          .map(
           detail ->
            new CoinChargingHistoryResponse(
             detail.getId(),
             detail.getAmount(),
             detail.getPrice(),
             detail.getPaymentDate()
            )).collect(Collectors.toList());
    }
}
