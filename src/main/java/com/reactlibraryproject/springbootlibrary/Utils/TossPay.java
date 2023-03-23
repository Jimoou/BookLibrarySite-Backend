package com.reactlibraryproject.springbootlibrary.Utils;

import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class TossPay {
    @Value("${toss.secret.key}")
    private String secretKey;

    private static String tossSecretKey;

    @PostConstruct
    private void init() {
        TossPay.tossSecretKey = secretKey;
    }

    public static ResponseEntity<String> pay(SuccessPaymentRequest paymentRequests) throws IOException, InterruptedException {
        String requestBody = createPaymentConfirmRequestBody(paymentRequests);

        HttpRequest request =
         HttpRequest.newBuilder()
          .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
          .header("Authorization", "Basic " + tossSecretKey)
          .header("Content-Type", "application/json")
          .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
          .build();

        HttpResponse<String> response =
         HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }

    private static String createPaymentConfirmRequestBody(SuccessPaymentRequest paymentRequests) {
        String paymentKey = paymentRequests.getPaymentKey();
        String orderId = paymentRequests.getOrderId();
        int amount = paymentRequests.getAmount();
        return String.format(
         "{\"paymentKey\":\"%s\",\"amount\":%d,\"orderId\":\"%s\"}", paymentKey, amount, orderId);
    }
}
