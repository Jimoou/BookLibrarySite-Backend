package com.reactlibraryproject.springbootlibrary.Controller;

import com.reactlibraryproject.springbootlibrary.ReponseModels.PaymentHistoryResponse;
import com.reactlibraryproject.springbootlibrary.RequestModels.AddPaymentRequest;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPaymentRequest;
import com.reactlibraryproject.springbootlibrary.Service.PaymentService;
import com.reactlibraryproject.springbootlibrary.Utils.ExtractJWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("https://localhost:3000")
@RestController
@AllArgsConstructor
@RequestMapping("/api/payment")
@Tag(name = "결제", description = "결제 API")
public class PaymentController {

  private PaymentService paymentService;

  @Operation(summary = "결제 내역 조회")
  @GetMapping("/secure/histories")
  public List<PaymentHistoryResponse> purchaseHistories(
      @RequestHeader(value = "Authorization") String token) throws Exception {
    String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
    return paymentService.paymentHistories(userEmail);
  }

  @Operation(summary = "결제 승인 전 DB에 추가")
  @PostMapping("/secure")
  public void addPendingPurchases(
      @RequestHeader(value = "Authorization") String token,
      @RequestBody List<AddPaymentRequest> paymentRequests)
      throws Exception {
    String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
    paymentService.addPendingPayments(userEmail, paymentRequests);
  }

  @Operation(summary = "결제 승인 전 오류시 DB에서 삭제")
  @DeleteMapping("/secure/delete/fail")
  public void deleteFailPurchase(@RequestHeader(value = "Authorization") String token)
      throws Exception {
    String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
    paymentService.deleteFailPayment(userEmail);
  }

  @Operation(summary = "결제 승인 API 호출", description = "API 호출 후 결제 내역 업데이트")
  @PutMapping("/secure/update")
  public void updateSuccessPayment(
      @RequestHeader(value = "Authorization") String token,
      @RequestBody SuccessPaymentRequest paymentRequests)
      throws Exception {
    String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
    paymentService.updateSuccessPayment(userEmail, paymentRequests);
  }
}
