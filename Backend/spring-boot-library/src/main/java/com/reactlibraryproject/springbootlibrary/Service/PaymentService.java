package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.BookRepository;
import com.reactlibraryproject.springbootlibrary.DAO.PaymentHistoryRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.PaymentHistory;
import com.reactlibraryproject.springbootlibrary.ReponseModels.PaymentHistoryResponse;
import com.reactlibraryproject.springbootlibrary.RequestModels.AddPaymentRequest;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPaymentRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PaymentService {

  private final PaymentHistoryRepository paymentHistoryRepository;
  private final BookRepository bookRepository;
  private final CartItemService cartItemService;

  public List<PaymentHistoryResponse> paymentHistories(String userEmail) {
    List<PaymentHistory> paymentHistory =
        paymentHistoryRepository.findPaymentByUserEmail(userEmail);
    Map<String, List<PaymentHistory>> paymentHistoryMap =
        groupPaymentHistoryByOrderId(paymentHistory);

    return createPaymentHistoryResponses(paymentHistoryMap);
  }

  private Map<String, List<PaymentHistory>> groupPaymentHistoryByOrderId(
      List<PaymentHistory> paymentHistory) {
    return paymentHistory.stream().collect(Collectors.groupingBy(PaymentHistory::getOrderId));
  }

  private List<PaymentHistoryResponse> createPaymentHistoryResponses(
      Map<String, List<PaymentHistory>> paymentHistoryMap) {
    return paymentHistoryMap.entrySet().stream()
        .map(this::createPaymentHistoryResponse)
        .collect(Collectors.toList());
  }

  private PaymentHistoryResponse createPaymentHistoryResponse(
      Map.Entry<String, List<PaymentHistory>> entry) {
    List<PaymentHistory> paymentHistories = entry.getValue();
    String orderName = getOrderName(paymentHistories);
    int totalPrice = getTotalPrice(paymentHistories);
    String paymentDate = paymentHistories.get(0).getPaymentDate();
    String orderId = paymentHistories.get(0).getOrderId();
    String status = paymentHistories.get(0).getStatus();

    return new PaymentHistoryResponse(
        entry.getKey(), orderName, totalPrice, paymentDate, orderId, status);
  }

  private String getOrderName(List<PaymentHistory> paymentHistories) {
    String orderName = paymentHistories.get(0).getTitle();
    if (paymentHistories.size() > 1) {
      orderName = orderName + "외" + (paymentHistories.size() - 1) + "건";
    }
    return orderName;
  }

  private int getTotalPrice(List<PaymentHistory> paymentHistories) {
    return paymentHistories.stream().mapToInt(PaymentHistory::getPrice).sum();
  }

  public void addPendingPayments(String userEmail, List<AddPaymentRequest> paymentRequests) {
    paymentRequests.forEach(paymentRequest -> processPaymentRequest(userEmail, paymentRequest));
  }

  private void processPaymentRequest(String userEmail, AddPaymentRequest paymentRequest) {
    try {
      Optional<Book> book = bookRepository.findById(paymentRequest.getBookId());
      PaymentHistory newPayment = createPaymentHistory(userEmail, paymentRequest, book.get());
      paymentHistoryRepository.save(newPayment);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private PaymentHistory createPaymentHistory(
      String userEmail, AddPaymentRequest paymentRequest, Book book) {
    return PaymentHistory.builder()
        .userEmail(userEmail)
        .title(book.getTitle())
        .author(book.getAuthor())
        .category(book.getCategory())
        .img(book.getImg())
        .publisher(book.getPublisher())
        .amount(paymentRequest.getAmount())
        .price(book.getPrice() * paymentRequest.getAmount())
        .cartItemId(paymentRequest.getCartItemId())
        .build();
  }

  public void deleteFailPayment(String userEmail) {
    paymentHistoryRepository.deleteByUserEmailAndStatusIsNull(userEmail);
  }

  public void updateSuccessPayment(String userEmail, SuccessPaymentRequest successPaymentRequest)
      throws Exception {
    List<PaymentHistory> pendingPayments =
        paymentHistoryRepository.findByUserEmailAndStatusIsNull(userEmail);

    if (successPaymentRequest.getTotalAmount() != getTotalPrice(pendingPayments)) {
      deleteFailPayment(userEmail);
      throw new Exception("잘못된 결제 요청입니다.");
    }

    pendingPayments.stream()
        .filter(payment -> successPaymentRequest.getStatus() != null)
        .forEach(
            payment -> updatePaymentAndDeleteCartItem(userEmail, successPaymentRequest, payment));
  }

  private void updatePaymentAndDeleteCartItem(
      String userEmail, SuccessPaymentRequest successPaymentRequest, PaymentHistory payment) {
    updatePayment(payment, successPaymentRequest);
    cartItemService.deleteCartItem(userEmail, payment.getCartItemId());
  }

  private void updatePayment(PaymentHistory payment, SuccessPaymentRequest successPaymentRequest) {
    payment.setPaymentKey(successPaymentRequest.getPaymentKey());
    payment.setOrderId(successPaymentRequest.getOrderId());
    payment.setPaymentDate(successPaymentRequest.getPaymentDate());
    payment.setStatus(successPaymentRequest.getStatus());
    paymentHistoryRepository.save(payment);
  }
}
