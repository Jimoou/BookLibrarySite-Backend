package com.reactlibraryproject.springbootlibrary.Service;

import com.reactlibraryproject.springbootlibrary.DAO.PaymentHistoryRepository;
import com.reactlibraryproject.springbootlibrary.Entity.Book;
import com.reactlibraryproject.springbootlibrary.Entity.PaymentHistory;
import com.reactlibraryproject.springbootlibrary.RequestModels.PendingPaymentRequest;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPaymentRequest;
import com.reactlibraryproject.springbootlibrary.Utils.BookFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("결제 서비스 테스트")
class PaymentServiceTest {

  @InjectMocks private PaymentService paymentService;
  private PendingPaymentRequest pendingPaymentRequest;
  private Book testBook;
  private PaymentHistory paymentHistory;
  @Mock private PaymentHistoryRepository paymentHistoryRepository;
  @Mock private BookFinder bookFinder;

  @BeforeEach
  void setUp() {
    testBook =
        Book.builder()
            .id(1L)
            .title("title")
            .author("author")
            .description("description")
            .copies(1)
            .copiesAvailable(1)
            .category("category")
            .img("img")
            .publisher("publisher")
            .price(100)
            .coin(0)
            .publicationDate("2023-03-18")
            .build();
    paymentHistory =
        new PaymentHistory(
            1L,
            "user@email.com",
            "title",
            "author",
            "category",
            "img",
            "publisher",
            1,
            100,
            null,
            null,
            null,
            null,
            1L);
    pendingPaymentRequest = new PendingPaymentRequest(1L, 1, 1L);
  }

  @Test
  @DisplayName("결제 승인 전 결제 내역 DB 저장 테스트")
  void addPendingPayments() {
    // Given
    List<PendingPaymentRequest> paymentRequests = Collections.singletonList(pendingPaymentRequest);
    when(bookFinder.bookFinder(any(Long.class))).thenReturn(testBook);

    // When
    paymentService.addPendingPayments("user@email.com", paymentRequests);

    // Then
    verify(bookFinder, times(1)).bookFinder(any(Long.class));
    verify(paymentHistoryRepository, times(1)).save(any(PaymentHistory.class));
  }

  @Test
  @DisplayName("결제 실패 테스트")
  void failPayment() {
    // Given
    String userEmail = "user@email.com";

    // When
    paymentService.failPayment(userEmail);

    // Then
    verify(paymentHistoryRepository, times(1)).deleteByUserEmailAndStatusIsNull(userEmail);
  }
}
