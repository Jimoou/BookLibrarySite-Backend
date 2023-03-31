package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.CustomExceptions.DifferentAmountRequestException;
import com.reactlibraryproject.springbootlibrary.CustomExceptions.TossPayResponseException;
import com.reactlibraryproject.springbootlibrary.DAO.PaymentHistoryRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.PaymentHistory;
import com.reactlibraryproject.springbootlibrary.ReponseModels.PaymentHistoryResponse;
import com.reactlibraryproject.springbootlibrary.RequestModels.PendingPaymentRequest;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPaymentRequest;
import com.reactlibraryproject.springbootlibrary.Utils.BookFinder;
import com.reactlibraryproject.springbootlibrary.Utils.TossPay;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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
    private CoinService coinService;

    /*도서 결제 내역*/
    public Map<String, List<PaymentHistoryResponse>> paymentHistoryResponses(String userEmail) {
        deleteFailedPayments(userEmail);
        List<PaymentHistory> paymentHistories =
         paymentHistoryRepository.findPaymentByUserEmail(userEmail);
        paymentHistories.sort(Comparator.comparing(PaymentHistory::getPaymentDate).reversed());

        return paymentHistories.stream()
         .map(
          paymentHistory ->
           new PaymentHistoryResponse(
            paymentHistory.getId(),
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

    /*결제 승인 API 호출 및 결과 처리*/
    public ResponseEntity<String> confirmPayment(
     String userEmail, SuccessPaymentRequest paymentRequests) {
        ResponseEntity<String> response = TossPay.pay(paymentRequests);
        if (response.getStatusCodeValue() == 200) {
            processPaymentResponse(userEmail, paymentRequests, response);
            return ResponseEntity.status(response.getStatusCode()).body("{\"message\": \"결제에 성공했습니다\" }");
        } else {
            throw new TossPayResponseException(response);
        }
    }

    /*결제 승인 API 호출 후 결과에 따른 로직 처리*/
    private void processPaymentResponse(String userEmail, SuccessPaymentRequest paymentRequests, ResponseEntity<String> response) {
        if (paymentRequests.getOrderId().startsWith("coin")) {
            coinService.coinPayment(userEmail, paymentRequests);
        } else {
            updatePayments(userEmail, response);
        }
    }

    /*실패한 결제 내역 삭제*/
    public void deleteFailedPayments(String userEmail) {
        paymentHistoryRepository.deleteByUserEmailAndStatusIsNull(userEmail);
    }

    /*대기 중인 결제 추가*/
    public void addPendingPayments(String userEmail, List<PendingPaymentRequest> paymentRequests) {
        paymentRequests.forEach(
         paymentRequest -> {
             Book book = bookFinder.bookFinder(paymentRequest.getBookId());
             addPendingPayment(userEmail, book, paymentRequest);
         });
    }

    /*대기 중인 결제 builder*/
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

    /*결제 승인 후 대기 중인 결제 내역 업데이트*/
    private void updatePayments(String userEmail, ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        String paymentKey = jsonObject.getString("paymentKey");
        String orderId = jsonObject.getString("orderId");
        String paymentDate = jsonObject.getString("approvedAt");
        String status = jsonObject.getString("status");

        List<PaymentHistory> pendingPaymentHistories =
         paymentHistoryRepository.findByUserEmailAndStatusIsNull(userEmail);

        int responseTotalPrice = jsonObject.getInt("totalAmount");
        int getTotalPrice = pendingPaymentHistories.stream().mapToInt(PaymentHistory::getPrice).sum();

        if (responseTotalPrice == getTotalPrice) {
            updatePendingPaymentsFromResponse(paymentKey, orderId, paymentDate, status, pendingPaymentHistories);
        } else {
            throw new DifferentAmountRequestException();
        }
    }

    /*API 응답에 따라 대기 중인 결제 내역 업데이트*/
    private void updatePendingPaymentsFromResponse(String paymentKey, String orderId, String paymentDate, String status, List<PaymentHistory> pendingPaymentHistories) {
        pendingPaymentHistories.forEach(
         pendingPayment -> {
             updatePendingPayment(paymentKey, orderId, paymentDate, status, pendingPayment);
             paymentHistoryRepository.save(pendingPayment);
             cartItemService.deleteCartItem(pendingPayment.getUserEmail(), pendingPayment.getCartItemId());
         });
    }

    /*대기 중인 결제 내역 업데이트 set*/
    private void updatePendingPayment(String paymentKey, String orderId, String paymentDate, String status, PaymentHistory pendingPayment) {
        pendingPayment.setPaymentKey(paymentKey);
        pendingPayment.setPaymentDate(paymentDate);
        pendingPayment.setOrderId(orderId);
        pendingPayment.setStatus(status);
    }
}
