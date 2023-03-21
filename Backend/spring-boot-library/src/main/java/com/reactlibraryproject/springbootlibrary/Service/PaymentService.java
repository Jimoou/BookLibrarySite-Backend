package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.CustomExceptions.DifferentAmountRequestException;
import com.reactlibraryproject.springbootlibrary.CustomExceptions.PaymentResponseException;
import com.reactlibraryproject.springbootlibrary.DAO.PaymentHistoryRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.PaymentHistory;
import com.reactlibraryproject.springbootlibrary.ReponseModels.PaymentHistoryResponse;
import com.reactlibraryproject.springbootlibrary.RequestModels.PendingPaymentRequest;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPaymentRequest;
import com.reactlibraryproject.springbootlibrary.Utils.BookFinder;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PaymentService {

  private PaymentHistoryRepository paymentHistoryRepository;
  private CartItemService cartItemService;
  private BookFinder bookFinder;
  private String tossSecretKey;

  public Map<String, List<PaymentHistoryResponse>> paymentHistoryResponses(String userEmail) {
    List<PaymentHistory> paymentHistories =
        paymentHistoryRepository.findPaymentByUserEmail(userEmail);

    return paymentHistories.stream()
        .map(
            paymentHistory ->
                new PaymentHistoryResponse(
                    paymentHistory.getTitle(),
                    paymentHistory.getAuthor(),
                    paymentHistory.getCategory(),
                    paymentHistory.getImg(),
                    paymentHistory.getPublisher(),
                    paymentHistory.getAmount(),
                    paymentHistory.getPrice(),
                    paymentHistory.getPaymentDate(),
                    paymentHistory.getOrderId()))
        .collect(Collectors.groupingBy(PaymentHistoryResponse::getPaymentDate));
  }

  public ResponseEntity<String> successPayment(
      String userEmail, SuccessPaymentRequest paymentRequests)
      throws IOException, InterruptedException {

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

    if (response.statusCode() == 200) {
      updatePayments(userEmail, response);
    } else {
      failPayment(userEmail);
      throw new PaymentResponseException(response);
    }
    return ResponseEntity.status(response.statusCode()).build();
  }

  public void failPayment(String userEmail) {
    paymentHistoryRepository.deleteByUserEmailAndStatusIsNull(userEmail);
  }

  private String createPaymentConfirmRequestBody(SuccessPaymentRequest paymentRequests) {
    String paymentKey = paymentRequests.getPaymentKey();
    String orderId = paymentRequests.getOrderId();
    int amount = paymentRequests.getAmount();
    return String.format(
        "{\"paymentKey\":\"%s\",\"amount\":%d,\"orderId\":\"%s\"}", paymentKey, amount, orderId);
  }

  public void addPendingPayments(String userEmail, List<PendingPaymentRequest> paymentRequests) {
    paymentRequests.forEach(
        paymentRequest -> {
          Book book = bookFinder.bookFinder(paymentRequest.getBookId());
          addPendingPayment(userEmail, book, paymentRequest);
        });
  }

  private void addPendingPayment(
      String userEmail, Book book, PendingPaymentRequest paymentRequest) {
    PaymentHistory paymentHistory =
        PaymentHistory.builder()
            .userEmail(userEmail)
            .title(book.getTitle())
            .author(book.getAuthor())
            .category(book.getCategory())
            .img(book.getImg())
            .publisher(book.getPublisher())
            .amount(paymentRequest.getAmount())
            .price(paymentRequest.getAmount() * book.getPrice())
            .cartItemId(paymentRequest.getCartItemId())
            .build();
    paymentHistoryRepository.save(paymentHistory);
  }

  private void updatePayments(String userEmail, HttpResponse<String> response) {
    JSONObject jsonObject = new JSONObject(response.body());
    String paymentKey = jsonObject.getString("paymentKey");
    String orderId = jsonObject.getString("orderId");
    String paymentDate = jsonObject.getString("approvedAt");
    String status = jsonObject.getString("status");

    List<PaymentHistory> pendingPaymentHistories =
        paymentHistoryRepository.findByUserEmailAndStatusIsNull(userEmail);

    int responseTotalPrice = jsonObject.getInt("totalAmount");
    int getTotalPrice = pendingPaymentHistories.stream().mapToInt(PaymentHistory::getPrice).sum();

    if (responseTotalPrice == getTotalPrice) {
      pendingPaymentHistories.forEach(
          pendingPayment -> {
            pendingPayment.setPaymentKey(paymentKey);
            pendingPayment.setPaymentDate(paymentDate);
            pendingPayment.setOrderId(orderId);
            pendingPayment.setStatus(status);
            paymentHistoryRepository.save(pendingPayment);
            cartItemService.deleteCartItem(userEmail, pendingPayment.getCartItemId());
          });
    } else {
      failPayment(userEmail);
      throw new DifferentAmountRequestException();
    }
  }
}
