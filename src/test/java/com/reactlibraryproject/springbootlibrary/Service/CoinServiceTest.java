package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.CoinRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Coin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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

    @Mock
    private CoinRepository coinRepository;

    @BeforeEach
    void setUp() {
        coin = Coin.builder()
         .id(1L)
         .userEmail("user@email.com")
         .amount(100)
         .build();
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
    @DisplayName("코인 충전 성공 테스트")
    void successPayment() {

    }

    @Test
    @DisplayName("코인으로 결제 테스트")
    void useCoin() {
        // Given
        when(coinRepository.findByUserEmail(anyString())).thenReturn(coin);
        int coinsToUse = 30;

        // When
        coinService.useCoin(coin.getUserEmail(), coinsToUse);

        // Then
        assertEquals(coin.getAmount(), 70);
    }
}