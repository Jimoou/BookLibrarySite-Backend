package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.CoinChargingHistoryRepository;
import com.reactlibraryproject.springbootlibrary.DAO.CoinRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Coin;
import com.reactlibraryproject.springbootlibrary.Entity.CoinChargingHistory;
import com.reactlibraryproject.springbootlibrary.ReponseModels.CoinChargingHistoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("코인 서비스 테스트")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class CoinServiceTest {
    @InjectMocks
    private CoinService coinService;

    public Coin coin;

    public List<CoinChargingHistory> coinHistories;

    @Mock
    private CoinRepository coinRepository;

    @Mock
    private CoinChargingHistoryRepository coinChargingHistoryRepository;

    @BeforeEach
    void setUp() {
        coin = Coin.builder()
         .id(1L)
         .userEmail("user@email.com")
         .amount(100)
         .build();
        CoinChargingHistory coinChargingHistory = CoinChargingHistory.builder()
         .price(10000)
         .userEmail("user@email.com")
         .amount(100)
         .paymentKey("paymentKey")
         .paymentDate("2023-03-24")
         .orderId("orderId")
         .status("DONE")
         .build();
        coinHistories = Collections.singletonList(coinChargingHistory);
    }

    @Test
    @DisplayName("유저 코인 수 테스트")
    void userCoins() {
        //Given
        when(coinRepository.findByUserEmail("user@email.com")).thenReturn(coin);

        //When
        int userCoins = coinService.userCoins("user@email.com");

        //Then
        assertEquals(100, userCoins);
    }


    @Test
    @DisplayName("코인 충전 내역 테스트")
    void coinHistories() {
        //Given
        when(coinChargingHistoryRepository.findByUserEmail(anyString())).thenReturn(coinHistories);

        //When
        List<CoinChargingHistoryResponse> historyResponseList = coinService.getCoinChargingHistory(anyString());

        //Then
        assertEquals(1, historyResponseList.size());
    }

}