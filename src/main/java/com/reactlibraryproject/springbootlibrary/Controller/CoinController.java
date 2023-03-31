package com.reactlibraryproject.springbootlibrary.Controller;

import com.reactlibraryproject.springbootlibrary.ReponseModels.CoinChargingHistoryResponse;
import com.reactlibraryproject.springbootlibrary.ReponseModels.CoinUsingHistoryResponse;
import com.reactlibraryproject.springbootlibrary.Service.CoinService;
import com.reactlibraryproject.springbootlibrary.Utils.ExtractJWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("https://springboot-library-add4e.web.app/")
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

    @Operation(summary = "코인 사용 내역 조회")
    @GetMapping("/secure/history/using")
    public List<CoinUsingHistoryResponse> getCoinUsingHistory(@RequestHeader(value = "Authorization") String token) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return coinService.getCoinUsingHistory(userEmail);
    }

    @Operation(summary = "코인 충전 내역")
    @GetMapping("/secure/history/charge")
    public List<CoinChargingHistoryResponse> getCoinChargingHistory(@RequestHeader(value = "Authorization") String token) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return coinService.getCoinChargingHistory(userEmail);
    }
}
