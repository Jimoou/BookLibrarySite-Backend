package com.reactlibraryproject.springbootlibrary.Controller;

import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPaymentRequest;
import com.reactlibraryproject.springbootlibrary.Service.CoinService;
import com.reactlibraryproject.springbootlibrary.Utils.ExtractJWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("https://localhost:3000")
@RestController
@AllArgsConstructor
@RequestMapping("/api/coins")
@Tag(name = "코인", description = "코인 사용 API")
public class CoinController {

    private CoinService coinService;

    @Operation(summary = "유저의 보유 코인 수")
    @GetMapping("/secure/count")
    public int userCoins(@RequestHeader(value = "Authorization") String token) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return coinService.userCoins(userEmail);
    }

    @Operation(summary = "코인 충전 성공")
    @GetMapping("/secure/confirm")
    public ResponseEntity<String> confirmPayment(
     @RequestHeader(value = "Authorization") String token,
     @RequestBody SuccessPaymentRequest paymentRequests) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return coinService.confirmPayment(userEmail, paymentRequests);
    }

    @Operation(summary = "코인 결제")
    @PutMapping("/secure/coin-pay")
    public void payWithCoin(@RequestHeader(value = "Authorization") String token, @RequestParam("amount") int coinsToUse) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        coinService.useCoin(userEmail, coinsToUse);
    }
}
