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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("https://localhost:3000")
@RestController
@AllArgsConstructor
@RequestMapping("/api/payment")
@Tag(name = "결제", description = "결제 API")
public class PaymentController {

  private PaymentService paymentService;

  @Operation(summary = "결제 내역 조회")
  @GetMapping("/secure/history")
  public Map<String, List<PaymentHistoryResponse>> paymentHistoryResponses(
      @RequestHeader(value = "Authorization") String token) {
    String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
    return paymentService.paymentHistoryResponses(userEmail);
  }

  @Operation(summary = "결제 승인 전 DB에 추가")
  @PostMapping("/secure")
  public void addPendingPayments(
      @RequestHeader(value = "Authorization") String token,
      @RequestBody List<PendingPaymentRequest> paymentRequests) {
    String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
    paymentService.addPendingPayments(userEmail, paymentRequests);
  }

  @Operation(summary = "결제 실패시 DB에서 삭제")
  @DeleteMapping("/secure/delete/fail")
  public void failPayment(@RequestHeader(value = "Authorization") String token) throws Exception {
    String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
    paymentService.failPayment(userEmail);
  }

  @Operation(summary = "결제 승인 API 호출", description = "API 호출 후 결제 내역 업데이트")
  @PostMapping("/secure/confirm")
  public ResponseEntity<String> successPayment(
      @RequestHeader(value = "Authorization") String token,
      @RequestBody SuccessPaymentRequest paymentRequests)
      throws Exception {
    String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
    return paymentService.successPayment(userEmail, paymentRequests);
  }
}
