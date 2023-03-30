package com.reactlibraryproject.springbootlibrary.Controller;

import com.reactlibraryproject.springbootlibrary.ReponseModels.PaymentHistoryResponse;
import com.reactlibraryproject.springbootlibrary.RequestModels.PendingPaymentRequest;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPaymentRequest;
import com.reactlibraryproject.springbootlibrary.Service.PaymentService;
import com.reactlibraryproject.springbootlibrary.Utils.ExtractJWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin("https://springboot-library-add4e.web.app/")
@RestController
@AllArgsConstructor
@RequestMapping("/api/payment-histories")
@Tag(name = "결제", description = "결제 API")
public class PaymentController {

    private PaymentService paymentService;

    @Operation(summary = "결제 내역 조회")
    @GetMapping("/secure")
    public Map<String, List<PaymentHistoryResponse>> paymentHistoryResponses(
     @RequestHeader(value = "Authorization") String token) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return paymentService.paymentHistoryResponses(userEmail);
    }

    @Operation(summary = "결제 승인 전 DB에 추가")
    @PostMapping("/secure/addpending")
    public void addPendingPayments(
     @RequestHeader(value = "Authorization") String token,
     @RequestBody List<PendingPaymentRequest> paymentRequests) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        paymentService.addPendingPayments(userEmail, paymentRequests);
    }

    @Operation(summary = "결제 실패 내역 삭제")
    @DeleteMapping("/secure/delete/fail")
    public void deleteFailPayment(@RequestHeader(value = "Authorization") String token) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        paymentService.deleteFailedPayments(userEmail);
    }

    @Operation(summary = "결제 승인 API 호출", description = "API 승인 후 결제 내역 업데이트")
    @PostMapping("/secure/confirm")
    public ResponseEntity<String> confirmPayment(
     @RequestHeader(value = "Authorization") String token,
     @RequestBody SuccessPaymentRequest paymentRequests) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return paymentService.confirmPayment(userEmail, paymentRequests);
    }
}
