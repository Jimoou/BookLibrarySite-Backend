package com.reactlibraryproject.springbootlibrary.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("코인 서비스 테스트")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class CoinServiceTest {
    @InjectMocks
    private CoinService coinService;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("유저 코인 수 테스트")
    void userCoins() {
    }

    @Test
    @DisplayName("코인 결제 성공 테스트")
    void successPayment() {
    }

    @Test
    @DisplayName("코인으로 결제 테스트")
    void payWithCoin() {
    }
}